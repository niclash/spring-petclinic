/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.zest.api.injection.scope.Service;
import org.apache.zest.api.injection.scope.Structure;
import org.apache.zest.api.unitofwork.UnitOfWork;
import org.apache.zest.api.unitofwork.UnitOfWorkFactory;
import org.apache.zest.api.value.ValueBuilder;
import org.apache.zest.bootstrap.AssemblyException;
import org.apache.zest.bootstrap.ModuleAssembly;
import org.apache.zest.entitystore.memory.MemoryEntityStoreService;
import org.apache.zest.index.rdf.assembly.RdfMemoryStoreAssembler;
import org.apache.zest.spi.uuid.UuidIdentityGeneratorService;
import org.apache.zest.test.AbstractZestTest;
import org.apache.zest.valueserialization.orgjson.OrgJsonValueSerializationService;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.dataloader.SampleDataLoader;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Vets;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.OwnerFactory;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetFactory;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.zest.api.value.ValueSerialization.Formats.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

/**
 * <p> Base class for {@link ClinicService} integration tests. </p> <p> Subclasses should specify Spring context
 * configuration using {@link ContextConfiguration @ContextConfiguration} annotation </p> <p>
 * AbstractclinicServiceTests and its subclasses benefit from the following services provided by the Spring
 * TestContext Framework: </p> <ul> <li><strong>Spring IoC container caching</strong> which spares us unnecessary set up
 * time between test execution.</li> <li><strong>Dependency Injection</strong> of test fixture instances, meaning that
 * we don't need to perform application context lookups. See the use of {@link Autowired @Autowired} on the <code>{@link
 * #clinicService clinicService}</code> instance variable, which uses autowiring <em>by
 * type</em>. <li><strong>Transaction management</strong>, meaning each test method is executed in its own transaction,
 * which is automatically rolled back by default. Thus, even if tests insert or otherwise change database state, there
 * is no need for a teardown or cleanup script. <li> An {@link org.springframework.context.ApplicationContext
 * ApplicationContext} is also inherited and can be used for explicit bean lookup if necessary. </li> </ul>
 */
public class ClinicServiceTests extends AbstractZestTest
{

    @Service
    protected ClinicService clinicService;

    @Structure
    private UnitOfWorkFactory uowf;

    @Test
    public void shouldFindOwnersByLastName()
    {
        Collection<Owner> owners = this.clinicService.findOwnerByLastName( "Davis" );
        assertThat( owners.size() ).isEqualTo( 2 );

        owners = this.clinicService.findOwnerByLastName( "Daviss" );
        assertThat( owners.isEmpty() );
    }

    @Test
    public void shouldFindSingleOwnerWithPet()
    {
        Owner owner = this.clinicService.findOwnerById( "o1" );
        assertThat( owner.lastName().get() ).startsWith( "Franklin" );
        assertThat( owner.pets().count() ).isEqualTo( 1 );
    }

    @Test
    public void shouldReturnAllOwnersInCaseLastNameIsEmpty()
    {
        Collection<Owner> owners = this.clinicService.findOwnerByLastName( "" );
        Iterable<String> names = owners.stream().map( owner -> owner.lastName().get() ).collect( Collectors.toList() );
        MatcherAssert.assertThat( names, containsInAnyOrder( "Franklin", "Davis", "Davis", "Rodriquez", "McTavish", "Coleman", "Black", "Escobito", "Schroeder", "Estaban" ) );
    }

    @Test
    @Transactional
    public void shouldInsertOwner()
    {
        Collection<Owner> owners = this.clinicService.findOwnerByLastName( "Schultz" );
        int found = owners.size();
        Owner owner = clinicService.createOwner( "Sam", "Schultz" );

        // In Zest, all Values are immutable, so we need to build a new Value.
        ValueBuilder<Owner> builder = valueBuilderFactory.newValueBuilderWithPrototype( owner );
        Owner proto = builder.prototype();
        proto.address().set( "4, Evans Street" );
        proto.city().set( "Wollongong" );
        proto.telephone().set( "4444444444" );
        this.clinicService.updateOwner( builder.newInstance() );
        assertThat( owner.identity().get() ).isNotEqualTo( "0" );

        owners = this.clinicService.findOwnerByLastName( "Schultz" );
        assertThat( owners.size() ).isEqualTo( found + 1 );
    }

    @Test
    @Transactional
    public void shouldUpdateOwner()
    {
        Owner owner = this.clinicService.findOwnerById( "o1" );
        String oldLastName = owner.lastName().get();
        String newLastName = oldLastName + "X";

        // In Zest, all Values are immutable, so we need to build a new Value.
        ValueBuilder<Owner> builder = valueBuilderFactory.newValueBuilderWithPrototype( owner );
        Owner proto = builder.prototype();
        proto.lastName().set( newLastName );
        this.clinicService.updateOwner( builder.newInstance() );

        // retrieving new name from database
        owner = this.clinicService.findOwnerById( "o1" );
        assertThat( owner.lastName().get() ).isEqualTo( newLastName );
    }

    @Test
    public void shouldFindPetWithCorrectId()
    {
        Pet pet7 = this.clinicService.findPetById( "p7" );
        assertThat( pet7.name().get() ).startsWith( "Samantha" );
        Owner owner = clinicService.findOwnerByPet( pet7 );
        assertThat( owner.firstName().get() ).isEqualTo( "Jean" );
    }

    @Test
    public void shouldFindAllPetTypes()
    {
        Collection<PetType> petTypes = clinicService.findPetTypes();
        for( PetType type : petTypes )
        {
            if( type.identity().get().equals( "t1" ) )
            {
                assertThat( type.name().get() ).isEqualTo( "cat" );
            }
            if( type.identity().get().equals( "t4" ) )
            {
                assertThat( type.name().get() ).isEqualTo( "snake" );
            }
        }
    }

    @Test
    @Transactional
    public void shouldInsertPetIntoDatabaseAndGenerateId()
    {
        Owner owner6 = clinicService.findOwnerById( "o6" );
        int found = owner6.pets().count();
        Collection<PetType> petTypes = clinicService.findPetTypes();
        PetType newPetType = petTypes.iterator().next();

        Pet pet = clinicService.createPet( owner6, "bowser" );
        // In Zest, all Values are immutable, so we need to build a new Value.
        ValueBuilder<Pet> builder = valueBuilderFactory.newValueBuilderWithPrototype( pet );
        Pet proto = builder.prototype();
        proto.birthDate().set( LocalDate.now() );
        proto.type().set( newPetType );
        clinicService.updatePet( builder.newInstance() );

        owner6 = clinicService.findOwnerById( "o6" );
        assertThat( owner6.pets().count() ).isEqualTo( found + 1 );

        owner6 = this.clinicService.findOwnerById( "o6" );
        assertThat( owner6.pets().count() ).isEqualTo( found + 1 );
    }

    @Test
    @Transactional
    public void shouldUpdatePetName()
        throws Exception
    {
        Pet pet7 = this.clinicService.findPetById( "p7" );
        String oldName = pet7.name().get();
        String newName = oldName + "X";

        // In Zest, all Values are immutable, so we need to build a new Value.
        ValueBuilder<Pet> builder = valueBuilderFactory.newValueBuilderWithPrototype( pet7 );
        Pet proto = builder.prototype();
        proto.name().set( newName );
        this.clinicService.updatePet( builder.newInstance() );

        pet7 = this.clinicService.findPetById( "p7" );
        assertThat( pet7.name().get() ).isEqualTo( newName );
    }

    @Test
    public void shouldFindVets()
    {
        try (UnitOfWork uow = unitOfWorkFactory.newUnitOfWork())
        {
            Vet vet = uow.get( Vet.class, "v3" );
            assertThat( vet.lastName().get() ).isEqualTo( "Douglas" );
            assertThat( vet.specialties().count() ).isEqualTo( 2 );
            assertThat( vet.specialties().get( 0 ).name().get() ).isEqualTo( "surgery" );
            assertThat( vet.specialties().get( 1 ).name().get() ).isEqualTo( "dentistry" );
        }
    }

    @Test
    @Transactional
    public void shouldAddNewVisitForPet()
    {
        Pet pet7 = this.clinicService.findPetById( "p7" );
        int found = pet7.visits().count();
        Visit visit = clinicService.visitVet( "p7", LocalDate.now(), "test" );

        pet7 = this.clinicService.findPetById( "p7" );
        assertThat( pet7.visits().count() ).isEqualTo( found + 1 );
        assertThat( visit.identity().get() ).isNotNull();
    }

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        serviceFinder.findService( SampleDataLoader.class ).get().loadData();
    }

    @Override
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.values( Person.class, Owner.class, Pet.class, PetType.class, Specialty.class, Vet.class, Vets.class, Visit.class );
        module.entities( Person.class, Owner.class, Pet.class, PetType.class, Specialty.class, Vet.class, Vets.class, Visit.class );
        module.services( SampleDataLoader.class ).instantiateOnStartup();
        module.services( PetFactory.class ).instantiateOnStartup();
        module.services( PetRepository.class ).instantiateOnStartup();
        module.services( OwnerFactory.class ).instantiateOnStartup();
        module.services( OwnerRepository.class ).instantiateOnStartup();
        module.services( VetRepository.class ).instantiateOnStartup();
        module.services( ClinicService.class ).instantiateOnStartup();
        module.services( MemoryEntityStoreService.class ).instantiateOnStartup();
        module.services( UuidIdentityGeneratorService.class );
        module.services( OrgJsonValueSerializationService.class ).taggedWith( JSON );
        new RdfMemoryStoreAssembler().assemble( module );
    }
}

package org.springframework.samples.petclinic.bootstrap;

import org.apache.zest.api.activation.ActivationException;
import org.apache.zest.api.service.ServiceReference;
import org.apache.zest.bootstrap.AssemblyException;
import org.apache.zest.bootstrap.ModuleAssembly;
import org.apache.zest.bootstrap.SingletonAssembler;
import org.apache.zest.entitystore.memory.MemoryEntityStoreService;
import org.apache.zest.index.rdf.assembly.RdfMemoryStoreAssembler;
import org.apache.zest.spi.uuid.UuidIdentityGeneratorService;
import org.apache.zest.valueserialization.orgjson.OrgJsonValueSerializationService;
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
import org.springframework.samples.petclinic.service.ClinicService;

import static org.apache.zest.api.value.ValueSerialization.Formats.JSON;

public class ZestApplicationAssembler extends SingletonAssembler
{
    /**
     * Creates a Zest Runtime instance containing one Layer with one Module.
     * The Layer will be named "Layer 1" and the Module will be named "Module 1". It is possible to add
     * additional layers and modules via the Assembler interface that must be implemented in the subclass of this
     * class.
     *
     * @throws AssemblyException   Either if the model can not be created from the disk, or some inconsistency in
     *                             the programming model makes it impossible to create it.
     * @throws ActivationException If the automatic {@code activate()} method is throwing this Exception..
     */
    public ZestApplicationAssembler()
        throws AssemblyException, ActivationException
    {
        super();
        ServiceReference<SampleDataLoader> serviceRef = serviceFinder().findService( SampleDataLoader.class );
        if( serviceRef.isAvailable() ){
            serviceRef.get().loadData();
        }
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

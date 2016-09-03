package org.springframework.samples.petclinic.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.zest.api.association.NamedAssociation;
import org.apache.zest.api.injection.scope.Service;
import org.apache.zest.api.injection.scope.Structure;
import org.apache.zest.api.query.Query;
import org.apache.zest.api.unitofwork.UnitOfWork;
import org.apache.zest.api.unitofwork.UnitOfWorkFactory;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.OwnerFactory;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetFactory;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VetRepository;

public class ClinicServiceMixin
    implements ClinicService

{
    @Service
    private PetFactory petFactory;

    @Service
    private PetRepository petRepository;

    @Service
    private OwnerFactory ownerFactory;

    @Service
    private OwnerRepository ownerRepository;

    @Service
    private VetRepository vetRepository;

    @Structure
    private UnitOfWorkFactory uowf;

    @Override
    public Collection<PetType> findPetTypes()
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        return petRepository
            .findPetTypes()
            .stream()
            .map( petType -> uow.toValue( PetType.class, petType ) )      // convert to value
            .collect( Collectors.toList() );
    }

    @Override
    public Owner findOwnerById( String id )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        return uow.toValue( Owner.class, ownerRepository.findById( id ) );
    }

    @Override
    public void updateOwner( Owner owner )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        uow.toEntity( Owner.class, owner );
    }

    @Override
    public Pet findPetById( String id )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        return uow.toValue( Pet.class, petRepository.findById( id ) );
    }

    @Override
    public List<Visit> findVisitsByPet( String petId )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        return uow.toValueList( petRepository.findById( petId ).visits() );
    }

    @Override
    public Pet createPet( Owner owner, String name )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        Pet pet = petFactory.create( name );
        NamedAssociation<Pet> pets = uow.toEntity( Owner.class, owner ).pets();
        if( pets.containsName( name ) )
        {
            String message = "Pet with the name '" + name + "' already exists with owner '" + owner.fullName() + "'";
            throw new RuntimeException( message );
        }
        pets.put( name, pet );
        return uow.toValue( Pet.class, pet );
    }

    @Override
    public void updatePet( Pet pet )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        uow.toEntity( Pet.class, pet );
    }

    @Override
    public Visit visitVet( String petId, LocalDate visitDate, String description )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        Pet pet = uow.get( Pet.class, petId );
        return uow.toValue( Visit.class, pet.visitVet( visitDate, description ) );
    }

    @Override
    public void updateVisit( Visit visit )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        uow.toEntity( Visit.class, visit );
    }

    @Override
    public Owner findOwnerByPet( Pet pet7 )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        Owner owner = uow.toEntity( Pet.class, pet7 ).owner().get();
        return uow.toValue( Owner.class, owner );
    }

    @Override
    public Collection<Vet> findVets()
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        return vetRepository.findAll()
            .stream()
            .map( entity -> uow.toValue( Vet.class, entity ))
            .collect( Collectors.toList() );
    }

    @Override
    public Owner createOwner( String firstName, String lastName )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        return uow.toValue( Owner.class, ownerFactory.create( firstName, lastName ) );
    }

    @Override
    public Collection<Owner> findOwnerByLastName( String lastName )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        Query<Owner> byLastName = ownerRepository.findByLastName( lastName );
        ArrayList<Owner> owners = new ArrayList<>();
        byLastName.iterator().forEachRemaining( ( e ) -> owners.add( uow.toValue( Owner.class, e ) ) );
        return owners;
    }
}

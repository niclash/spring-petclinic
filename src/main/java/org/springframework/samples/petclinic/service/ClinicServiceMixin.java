package org.springframework.samples.petclinic.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.zest.api.injection.scope.Service;
import org.apache.zest.api.injection.scope.Structure;
import org.apache.zest.api.query.Query;
import org.apache.zest.api.unitofwork.UnitOfWork;
import org.apache.zest.api.unitofwork.UnitOfWorkFactory;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
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
    public Pet findPetById( String id )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        return uow.toValue( Pet.class, petRepository.findById( id ) );
    }

    @Override
    public void createPet( String name )
    {
        petFactory.create( name );
    }

    @Override
    public void createVisit( String petId, LocalDate visitDate, String description )
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        Pet pet = uow.get( Pet.class, petId );
        pet.visitVet( visitDate, description );
    }

    @Override
    public Collection<Vet> findVets()
    {
        UnitOfWork uow = uowf.currentUnitOfWork();
        return uow.toValueList( vetRepository.findAll() );
    }

    @Override
    public void createOwner( String firstName, String lastName )
    {
        ownerFactory.create( firstName, lastName );
    }

    @Override
    public Collection<Owner> findOwnerByLastName( String lastName )
    {
        Query<Owner> byLastName = ownerRepository.findByLastName( lastName );
        ArrayList<Owner> owners = new ArrayList<>();
        byLastName.iterator().forEachRemaining( owners::add );
        return owners;
    }
}

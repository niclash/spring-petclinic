package org.springframework.samples.petclinic.dataloader;

import java.time.LocalDate;
import org.apache.zest.api.concern.Concerns;
import org.apache.zest.api.entity.EntityBuilder;
import org.apache.zest.api.injection.scope.Structure;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.api.unitofwork.UnitOfWork;
import org.apache.zest.api.unitofwork.UnitOfWorkFactory;
import org.apache.zest.api.unitofwork.concern.UnitOfWorkConcern;
import org.apache.zest.api.unitofwork.concern.UnitOfWorkPropagation;
import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;

@Mixins( SampleDataLoader.Mixin.class )
@Concerns( UnitOfWorkConcern.class )
public interface SampleDataLoader
{
    @UnitOfWorkPropagation
    void loadData();

    class Mixin implements SampleDataLoader
    {

        @Structure
        private UnitOfWorkFactory uowf;

        @Override
        public void loadData()
        {
            Vet carter = createVet( "v1", "James", "Carter" );
            Vet leary = createVet( "v2", "Helen", "Leary" );
            Vet douglas = createVet( "v3", "Linda", "Douglas" );
            Vet ortega = createVet( "v4", "Rafael", "Ortega" );
            Vet stevens = createVet( "v5", "Henry", "Stevens" );
            Vet jenkins = createVet( "v6", "Sharon", "Jenkins" );

            Specialty radiology = createNamedEntity( "s1", "radiology", Specialty.class );
            Specialty surgery = createNamedEntity( "s2", "surgery", Specialty.class );
            Specialty dentistry = createNamedEntity( "s3", "dentistry", Specialty.class );

            leary.specialties().add( radiology );
            douglas.specialties().add( surgery );
            douglas.specialties().add( dentistry );
            ortega.specialties().add( surgery );
            stevens.specialties().add( radiology );

            PetType cat = createNamedEntity( "t1", "cat", PetType.class );
            PetType dog = createNamedEntity( "t2", "dog", PetType.class );
            PetType lizard = createNamedEntity( "t3", "lizard", PetType.class );
            PetType snake = createNamedEntity( "t4", "snake", PetType.class );
            PetType bird = createNamedEntity( "t5", "bird", PetType.class );
            PetType hamster = createNamedEntity( "t6", "hamster", PetType.class );

            Owner franklin = createOwner( "o1", "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023" );
            Owner bdavis = createOwner( "o2", "Betty", "Davis", "638 Cardinal Ave.", "Sun Prairie", "6085551749" );
            Owner rodriguez = createOwner( "o3", "Eduardo", "Rodriquez", "2693 Commerce St.", "McFarland", "6085558763" );
            Owner hdavis = createOwner( "o4", "Harold", "Davis", "563 Friendly St.", "Windsor", "6085553198" );
            Owner mctavish = createOwner( "o5", "Peter", "McTavish", "2387 S. Fair Way", "Madison", "6085552765" );
            Owner coleman = createOwner( "o6", "Jean", "Coleman", "105 N. Lake St.", "Monona", "6085552654" );
            Owner black = createOwner( "o7", "Jeff", "Black", "1450 Oak Blvd.", "Monona", "6085555387" );
            Owner escobito = createOwner( "o8", "Maria", "Escobito", "345 Maple St.", "Madison", "6085557683" );
            Owner schroeder = createOwner( "o9", "David", "Schroeder", "2749 Blackhawk Trail", "Madison", "6085559435" );
            Owner estaban = createOwner( "o10", "Carlos", "Estaban", "2335 Independence La.", "Waunakee", "6085555487" );

            Pet leo = createPet( "p1", "Leo", LocalDate.of( 2010, 9, 1 ), cat, franklin );
            Pet basil = createPet( "p2", "Basil", LocalDate.of( 2012, 8, 6 ), hamster, bdavis );
            Pet rosy = createPet( "p3", "Rosy", LocalDate.of( 2011, 4, 17 ), dog, rodriguez );
            Pet jewel = createPet( "p4", "Jewel", LocalDate.of( 2010, 3, 7 ), dog, rodriguez );
            Pet iggy = createPet( "p5", "Iggy", LocalDate.of( 2010, 11, 30 ), lizard, hdavis );
            Pet george = createPet( "p6", "George", LocalDate.of( 2010, 1, 20 ), snake, mctavish );
            Pet samantha = createPet( "p7", "Samantha", LocalDate.of( 2012, 9, 4 ), cat, coleman );
            Pet max = createPet( "p8", "Max", LocalDate.of( 2012, 9, 4 ), cat, coleman );
            Pet lucky1 = createPet( "p9", "Lucky", LocalDate.of( 2011, 8, 6 ), bird, black );
            Pet mulligan = createPet( "p10", "Mulligan", LocalDate.of( 2007, 2, 24 ), dog, escobito );
            Pet freddy = createPet( "p11", "Freddy", LocalDate.of( 2010, 3, 9 ), bird, schroeder );
            Pet lucky2 = createPet( "p12", "Lucky", LocalDate.of( 2010, 6, 24 ), dog, estaban );
            Pet sly = createPet( "p13", "Sly", LocalDate.of( 2012, 6, 8 ), cat, estaban );

            createVisit( "vv1", george, LocalDate.of( 2013, 1, 1 ), "rabies shot" );
            createVisit( "vv2", samantha, LocalDate.of( 2013, 1, 2 ), "rabies shot" );
            createVisit( "vv3", samantha, LocalDate.of( 2013, 1, 3 ), "neutered" );
            createVisit( "vv4", george, LocalDate.of( 2013, 1, 4 ), "spayed" );
        }

        private <T extends NamedEntity> T createNamedEntity( String uuid, String name, Class<T> type )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            EntityBuilder<T> builder = uow.newEntityBuilder( type, uuid );
            T proto = builder.instance();
            proto.name().set( name );
            return builder.newInstance();
        }

        private Vet createVet( String uuid, String firstName, String lastName )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            EntityBuilder<Vet> builder = uow.newEntityBuilder( Vet.class, uuid );
            Vet proto = builder.instance();
            proto.firstName().set( firstName );
            proto.lastName().set( lastName );
            return builder.newInstance();
        }

        private Owner createOwner( String uuid,
                                   String firstName,
                                   String lastName,
                                   String address,
                                   String city,
                                   String phone
        )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            EntityBuilder<Owner> builder = uow.newEntityBuilder( Owner.class, uuid );
            Owner proto = builder.instance();
            proto.firstName().set( firstName );
            proto.lastName().set( lastName );
            proto.telephone().set( phone );
            proto.address().set( address );
            proto.city().set( city );
            return builder.newInstance();
        }

        private Pet createPet( String uuid, String name, LocalDate birthdate, PetType petType, Owner owner )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            EntityBuilder<Pet> builder = uow.newEntityBuilder( Pet.class, uuid );
            Pet proto = builder.instance();
            proto.name().set( name );
            proto.birthDate().set( birthdate );
            proto.type().set( petType );
            proto.owner().set( owner );
            return builder.newInstance();
        }

        private Visit createVisit( String uuid, Pet pet, LocalDate visitDate, String description )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            EntityBuilder<Visit> builder = uow.newEntityBuilder( Visit.class, uuid );
            Visit proto = builder.instance();
            proto.description().set( description );
            proto.date().set( visitDate );
            Visit visit = builder.newInstance();
            pet.visits().add( visit );
            return visit;
        }
    }
}

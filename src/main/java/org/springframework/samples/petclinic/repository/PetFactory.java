package org.springframework.samples.petclinic.repository;

import org.apache.zest.api.concern.Concerns;
import org.apache.zest.api.entity.EntityBuilder;
import org.apache.zest.api.injection.scope.Structure;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.api.unitofwork.UnitOfWork;
import org.apache.zest.api.unitofwork.UnitOfWorkFactory;
import org.apache.zest.api.unitofwork.concern.UnitOfWorkConcern;
import org.apache.zest.api.unitofwork.concern.UnitOfWorkPropagation;
import org.springframework.samples.petclinic.model.Pet;

@Concerns( UnitOfWorkConcern.class )
@Mixins( PetFactory.Mixin.class )
public interface PetFactory
{
    @UnitOfWorkPropagation
    Pet create( String name );

    class Mixin
        implements PetFactory
    {
        @Structure
        private UnitOfWorkFactory uowf;

        @Override
        public Pet create( String name )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            EntityBuilder<Pet> builder = uow.newEntityBuilder( Pet.class );
            builder.instance().name().set( name );
            return builder.newInstance();
        }
    }
}

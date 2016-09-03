package org.springframework.samples.petclinic.repository;

import org.apache.zest.api.concern.Concerns;
import org.apache.zest.api.entity.EntityBuilder;
import org.apache.zest.api.injection.scope.Structure;
import org.apache.zest.api.unitofwork.UnitOfWork;
import org.apache.zest.api.unitofwork.UnitOfWorkFactory;
import org.apache.zest.api.unitofwork.concern.UnitOfWorkConcern;
import org.apache.zest.api.unitofwork.concern.UnitOfWorkPropagation;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Person;

@Concerns( UnitOfWorkConcern.class )
public interface OwnerFactory
{
    @UnitOfWorkPropagation
    Owner create( String firstName, String lastName );

    class Mixin
        implements OwnerFactory
    {
        @Structure
        private UnitOfWorkFactory uowf;

        @Override
        public Owner create( String firstName, String lastName )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            EntityBuilder<Owner> builder = uow.newEntityBuilder( Owner.class );
            Person p = builder.instance();
            p.lastName().set( lastName );
            p.firstName().set( firstName );
            return builder.newInstance();
        }
    }
}

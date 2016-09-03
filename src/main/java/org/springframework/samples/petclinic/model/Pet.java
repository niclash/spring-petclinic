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
package org.springframework.samples.petclinic.model;

import java.time.LocalDate;
import org.apache.zest.api.association.Association;
import org.apache.zest.api.association.ManyAssociation;
import org.apache.zest.api.common.Optional;
import org.apache.zest.api.common.UseDefaults;
import org.apache.zest.api.concern.Concerns;
import org.apache.zest.api.entity.EntityBuilder;
import org.apache.zest.api.injection.scope.Structure;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.api.property.Property;
import org.apache.zest.api.unitofwork.UnitOfWork;
import org.apache.zest.api.unitofwork.UnitOfWorkFactory;
import org.apache.zest.api.unitofwork.concern.UnitOfWorkConcern;
import org.apache.zest.api.unitofwork.concern.UnitOfWorkPropagation;

@Mixins( Pet.Mixin.class )
@Concerns( UnitOfWorkConcern.class)
public interface Pet extends NamedEntity {

    @UnitOfWorkPropagation
    void visitVet( LocalDate visitDate, String description );

    @Optional
    Property<LocalDate> birthDate();

    @Optional
    Association<PetType> type();

    @Optional
    Association<Owner> owner();

    @UseDefaults
    ManyAssociation<Visit> visits();

    abstract class Mixin
             implements Pet
    {
        @Structure
        private UnitOfWorkFactory uowf;

        @Override
        public void visitVet( LocalDate visitDate, String description )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            EntityBuilder<Visit> builder = uow.newEntityBuilder( Visit.class );
            Visit prototype = builder.instance();
            prototype.date().set( visitDate );
            prototype.description().set( description );
            Visit visit = builder.newInstance();
            visits().add( visit );
        }
    }
}

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
package org.springframework.samples.petclinic.repository;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.zest.api.concern.Concerns;
import org.apache.zest.api.injection.scope.Structure;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.api.query.Query;
import org.apache.zest.api.query.QueryBuilder;
import org.apache.zest.api.query.QueryBuilderFactory;
import org.apache.zest.api.unitofwork.UnitOfWork;
import org.apache.zest.api.unitofwork.UnitOfWorkFactory;
import org.apache.zest.api.unitofwork.concern.UnitOfWorkConcern;
import org.apache.zest.api.unitofwork.concern.UnitOfWorkPropagation;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Owner;

import static org.apache.zest.api.query.QueryExpressions.eq;
import static org.apache.zest.api.query.QueryExpressions.templateFor;

/**
 * Repository class for <code>Owner</code> domain objects All method names are compliant with Spring Data naming
 * conventions so this interface can easily be extended for Spring Data See here: http://static.springsource.org/spring-data/jpa/docs/current/reference/html/jpa.repositories.html#jpa.query-methods.query-creation
 */
@Concerns( UnitOfWorkConcern.class )
@Mixins( OwnerRepository.Mixin.class )
public interface OwnerRepository
{

    /**
     * Retrieve <code>Owner</code>s from the data store by last name, returning all owners whose last name <i>starts</i>
     * with the given name.
     *
     * @param lastName Value to search for
     *
     * @return a <code>Collection</code> of matching <code>Owner</code>s (or an empty <code>Collection</code> if none
     * found)
     */
    @UnitOfWorkPropagation
    Collection<Owner> findByLastName( String lastName )
        throws DataAccessException;

    /**
     * Retrieve an <code>Owner</code> from the data store by id.
     *
     * @param id the id to search for
     *
     * @return the <code>Owner</code> if found
     *
     * @throws org.springframework.dao.DataRetrievalFailureException if not found
     */
    @UnitOfWorkPropagation
    Owner findById( int id )
        throws DataAccessException;

    class Mixin
        implements OwnerRepository
    {

        @Structure
        private UnitOfWorkFactory uowf;

        @Structure
        private QueryBuilderFactory qbf;

        @Override
        public Collection<Owner> findByLastName( String lastName )
            throws DataAccessException
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<Owner> builder = qbf.newQueryBuilder( Owner.class );
            Owner template = templateFor( Owner.class );
            builder.where( eq( template.lastName(), lastName ) );
            Query<Owner> query = uow.newQuery( builder );
            ArrayList<Owner> owners = new ArrayList<>();
            query.iterator().forEachRemaining( owners::add );
            return owners;
        }

        @Override
        public Owner findById( int id )
            throws DataAccessException
        {
            return uowf.currentUnitOfWork().get( Owner.class, String.valueOf( id ) );
        }
    }
}

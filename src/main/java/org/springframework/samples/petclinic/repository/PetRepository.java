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
import java.util.List;
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
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;

/**
 * Repository class for <code>Pet</code> domain objects All method names are compliant with Spring Data naming
 * conventions so this interface can easily be extended for Spring Data See here: http://static.springsource.org/spring-data/jpa/docs/current/reference/html/jpa.repositories.html#jpa.query-methods.query-creation
 */
@Mixins( PetRepository.Mixin.class )
@Concerns( UnitOfWorkConcern.class )
public interface PetRepository
{

    /**
     * Retrieve all <code>PetType</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>PetType</code>s
     */
    @UnitOfWorkPropagation
    List<PetType> findPetTypes()
        throws DataAccessException;

    /**
     * Retrieve a <code>Pet</code> from the data store by id.
     *
     * @param id the id to search for
     *
     * @return the <code>Pet</code> if found
     *
     * @throws org.springframework.dao.DataRetrievalFailureException if not found
     */
    @UnitOfWorkPropagation
    Pet findById( int id )
        throws DataAccessException;

    class Mixin
        implements PetRepository
    {

        @Structure
        private QueryBuilderFactory qbf;

        @Structure
        private UnitOfWorkFactory uowf;

        @Override
        public List<PetType> findPetTypes()
            throws DataAccessException
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            QueryBuilder<PetType> builder = qbf.newQueryBuilder( PetType.class );
            Query<PetType> query = uow.newQuery( builder );
            ArrayList<PetType> types = new ArrayList<>();
            query.iterator().forEachRemaining( types::add );
            return types;
        }

        @Override
        public Pet findById( int id )
            throws DataAccessException
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            return  uow.get( Pet.class, String.valueOf( id ) );
        }
    }
}

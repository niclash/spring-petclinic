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
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.api.unitofwork.concern.UnitOfWorkPropagation;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;

/**
 * Mostly used as a facade so all controllers have a single point of entry, and that it is values returned, not
 * entities.
 */
@Mixins(ClinicServiceMixin.class)
public interface ClinicService
{

    @UnitOfWorkPropagation
    Collection<PetType> findPetTypes();

    @UnitOfWorkPropagation
    Owner findOwnerById( String id );

    @UnitOfWorkPropagation
    Pet findPetById( String id );

    @UnitOfWorkPropagation
    void createPet( String name );

    @UnitOfWorkPropagation
    void createVisit( String petId, LocalDate visitDate, String description );

    @UnitOfWorkPropagation
    Collection<Vet> findVets();

    @UnitOfWorkPropagation
    void createOwner( String firstName, String lastName );

    @UnitOfWorkPropagation
    Collection<Owner> findOwnerByLastName( String lastName );
}

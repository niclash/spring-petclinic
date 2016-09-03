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
package org.springframework.samples.petclinic.web;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OwnerResource
{

    private final ClinicService clinicService;

    @Autowired
    public OwnerResource( ClinicService clinicService )
    {
        this.clinicService = clinicService;
    }

    @InitBinder
    public void setAllowedFields( WebDataBinder dataBinder )
    {
        dataBinder.setDisallowedFields( "id" );
    }

    private Owner retrieveOwner( String ownerId )
    {
        return this.clinicService.findOwnerById( ownerId );
    }

    /**
     * Create Owner
     */
    @RequestMapping( value = "/owner", method = RequestMethod.POST )
    @ResponseStatus( HttpStatus.CREATED )
    public void createOwner( @RequestBody Owner owner )
    {
        this.clinicService.updateOwner( owner );
        // TODO: need to handle failure
    }

    /**
     * Read single Owner
     */
    @RequestMapping( value = "/owner/{ownerId}", method = RequestMethod.GET )
    public Owner findOwner( @PathVariable( "ownerId" ) String ownerId )
    {
        return retrieveOwner( ownerId );
    }

    /**
     * Read List of Owners
     */
    @RequestMapping( value = "/owner/list", method = RequestMethod.GET )
    public Collection<Owner> findOwnerCollection( @RequestParam( "lastName" ) String ownerLastName )
    {
        if( ownerLastName == null )
        {
            ownerLastName = "";
        }

        Collection<Owner> results = this.clinicService.findOwnerByLastName( ownerLastName );
        if( results.isEmpty() )
        {
            return null;
        }
        else
        {
            return results;
        }
    }

    /**
     * Update Owner
     */
    @RequestMapping( value = "/owner/{ownerId}", method = RequestMethod.PUT )
    public Owner updateOwner( @PathVariable( "ownerId" ) String ownerId, @RequestBody Owner ownerRequest )
    {
        if( ownerId.equals( ownerRequest.identity().get() ) )
        {
            clinicService.updateOwner( ownerRequest );
            return clinicService.findOwnerById( ownerId );
            // TODO: need to handle failure
        }
        else
        {
            // handle invalid request.
            throw new RuntimeException( "Invalid ownerRequest body for owner '" + ownerId + "'" );
        }
    }
}

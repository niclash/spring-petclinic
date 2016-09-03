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

import org.apache.zest.api.entity.Identity;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.api.property.Property;

@Mixins( Person.Mixin.class )
public interface Person extends Identity
{

    Property<String> firstName();

    Property<String> lastName();

    String fullName();

    abstract class Mixin
        implements Person
    {
        @Override
        public String fullName()
        {
            return firstName().get() + " " + lastName().get();
        }
    }
}

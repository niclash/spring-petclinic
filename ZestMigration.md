# Migration Log
This document will be filled in as the migration of this Spring PetClinic
into an Apache Zest application with minimal changes.

## Preamable
I am starting with the AngularJS branch, as I think it looks neater.
However, the original code doesn't work fully; A nasty stack trace
for the ERROR button, instead of a nice render.

## Maven Dependencies
We need to add Apache Zest, and I am going to use a local build, i.e.
version 0, for this. SO, it is required that if you want to recreate
these steps you need to make a local build of Apache Zest first.

We add the minimal dependencies first.

    <dependency>
        <groupId>org.apache.zest.core</groupId>
    <artifactId>org.apache.zest.core.api</artifactId>
        <version>${zest.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.zest.core</groupId>
        <artifactId>org.apache.zest.core.bootstrap</artifactId>
        <version>${zest.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.zest.core</groupId>
        <artifactId>org.apache.zest.core.runtime</artifactId>
        <version>${zest.version}</version>
    </dependency>

## Start with the model.

The Pet Clinic model is a very simple one.
* BaseEntity
* NamedEntity
* Owner
* Person
* Pet
* PetType
* Specialty
* Vet
* Vets
* Visits

Well, those can all be directly converted to EntityComposites. We can
throw away BaseEntity and simply use Identity (or EntityComposite)
interface instead. The second thing we should get rid of is all the
boilerplate code, i.e. setters, getters and what not. That leaves us
with a set of small intefaces, Zest STYLE.

That was really easy. We end up with very tight state objects as a
start. I suspect that there should be a little bit more behavior in there
and we might make the state private later.



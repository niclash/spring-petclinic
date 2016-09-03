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

Here are the resulting classes.

    public interface NamedEntity
    {
        Property<String> name();
    }

    public interface Owner extends Person {
        Property<String> address();
        Property<String> city();
        Property<String> telephone();
        NamedAssociation<Pet> pets();
    }

    public interface Person {
        Property<String> firstName();
        Property<String>  lastName();
    }

    public interface Pet extends NamedEntity {
        Property<LocalDate> birthDate();
        Association<PetType> type();
        Association<Owner> owner();
        ManyAssociation<Visit> visits();
    }

    public interface PetType extends NamedEntity {}

    public interface Specialty extends NamedEntity {}

    public interface Vet extends Person {
        ManyAssociation<Specialty> specialties();
    }

    public interface Vets {
        ManyAssociation<Vet> vets();
    }

    public interface Visit {
        Property<LocalDate> date();
        Property<String> description();
        Property<Pet> pet();
    }

The interesting bit is the ```@Digits``` annotation, which we should
implement with Zest constraints. But let's look at that later.

This concludes the model changes for now.

## Look at Repository package
There are 4 repositories, with 3 implementations each. Wow! And the
complexity of those implementations vary a lot. And I guess Spring Data
is there to show off how neat it is. Well, we will show how neat it
could have been.

Let's remove all the implementations, because we don't need that.

They contain a bunch of ```find``` methods and some has a ```save```
method as well. We will actually need to add a Mixin for each of
these. But some doesn't make sense, as "saving a visit". Let's take one
repository at a time.

### VetRepository
Finding all Vets and no other find methods. We use ManyAssociation
inside a single Vets entity as the way to accomplish this.

### OwnerRepository
It can find by last name and by id. Let's implement the first with Query
and the other is a straight lookup.

It also has a ```saveOwner()``` method, which doesn't make sense. We
need a factory instead. So, let's add an OwnerFactory.

Then we add implementation of the two remaining methods in the
```OwnerRepository```, and we are done with that.

### PetRepository
This is more or less the same work as with the OwnerRepository above.
A new factory and implementations for findPetTypes and findById.

### VisitRepository
This is completely unnecessary. The visits of a given Pet is found
inside the Pet. And the factory of the ```Visit``` could also be in the
Pet class, where it kind of belong.

## Service layer
The service layer is pathetic. A bunch of methods in the wrong place.
But instead of fixing the design issue, we should first convert it to
Zest code.

Implementing a new ClinicServiceMixin took about 10 minutes.

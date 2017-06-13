# Jarecord(JPA Active Record)

Jarecord is a Java implementation of Active Record Pattern, inspired mainly in the Rails Active Record. Fully implemented with JPA and CDI.

## How to use

Basically you could extends your Entity class with this way:

```java
@Entity
public class Sample extends ActiveRecord<Sample> {
```

So you `Sample` class/object could use:

```java
/* To persist */
Sample sample = new Sample();
sample.setTitle("Hello World");
sample.create();

/* To delete one register */
sample.destroy();

/* Update is so easy */
sample.setTitle("Hii!");

/* Finder methods */

// Where 1L is primary key
Sample.find(1L);

// params is a HashMap
Sample.where(params);

Sample.all();

Options options = new Options("title", "Hello %");
// returns a javax.persistence.Query
Sample.findBy(options);
```

### Unstable! Come back here later.

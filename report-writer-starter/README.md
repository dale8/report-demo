# How this starter works

Under `src/main/resources/META-INF/` folder there is file `spring.factories`. It specifies main class of this starter that will be called by Spring Boot on application startup. It is `ReportWriterConfig` class.

`ReportWriterConfig` is a configuration class that creates and sets up all beans needed for this starter to work. It defines two service beans and all available writer implementations.

### Service beans

First `TranslatableDTORegistry` bean is created. That holds map where as a key we have a certain DTO.class and as a value another map, that holds translation for the field of the DTO and a getter method to be called to get a value for this field. `TranslatableDTORegistry` also exposes methods to get translations and register new translations.

After `TranslatableDTORegistry` bean was created it is being filled by `TranslatableDTORegistrarBeanPostProcessor` bean. 

`TranslatableDTORegistrarBeanPostProcessor` needs to scan classpath in order to find all classes annotated with `@TranslatableDTO`. For this it needs to know the base package where to start the scanning, so we operate under the assumption that our base package is a package where class annotated with `@SpringBootApplication` resides. This is the reason for limitation that all the DTOs to be translated have to be in the same package (or any of its subpackages).

Each class annotated with `@TranslatableDTO` we run through `FieldNameTranslationAnnotationProcessor`. It gets all the getter methods of DTO that are named according to the convention (i.e. names start with 'get' or 'is'), and checks whether they are annotated with `@FieldNameTranslation` annotation. If no, then it is checked if there is a field for this getter, and if yes, whether the field is annotated with `@FieldNameTranslation`.

If `value` field of `@FieldNameTranslation` is set then it is used. If `value` is not set or `@FieldNameTranslation` is not present, then translated name is inferred from getter name; 'get' or 'is' start is dropped, the rest is split to words by camel case.

If `ignored` field of `@FieldNameTranslation` is set to `true`, then the field (getter) will not be added to map with translated names and getters, and it will not be called by writer as a consequense.

When all the translated names and according getters (excluding ignored) have been gathered, they are sorted by `order` field of `@FieldNameTranslation`.

After that, the pairs of translated names and according getters are collected to map and returned to `TranslatableDTORegistrarBeanPostProcessor`, which registers them with the `TranslatableDTORegistry`.

### Writer beans

After `TranslatableDTORegistry` is created and filled it is supplied as a costructor argument to writer implementation beans.

When writer is asked to write some data, it gets the class of these data and asks the registry for translated names and getters for this class. Translated names are used to form the header for a table, and getters are used to get values for the cells of a table.

If DTO supplied to writer is not registered with `TranslatableDTORegistry`, writer will get the translations and register them by itself. So it is not mandatory to annotate DTOs with `@TranslatableDTO`, although it will provoke running introspection at runtime, which will trade marginally better startup time for marginally slower time of first response for given DTO.

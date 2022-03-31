# Report Writer Demo

The purpose of this project is to demostrate one of the possibilities of expanding Spring Framework with custom annotations.  
Extension is made in form of starter for Spring Boot.

## Structure

This repository consists of two folders:  
- report-demo-parent - simple REST service to demostrate possible usage of starter
- report-demo-starter - starter itself

## What is starter for

Sometimes there is a need to present some data in a table oriented file such as .csv or .xls, like some kind of report or statistics. This kind of presentation usually requires a header describing data stored in
according column. Header can be attached manually somewhere in code, but then in case of changing form of the report it would be needed not only to adjust DTO holding data projection, but also change logic attaching a header. And then there is a problem of diferent laguages: suppose you have to display header in a laguage different from english.  

Starter is designed to address this problem. It provides two annotations:  
- `@TranslatableDTO` to mark DTO's to be presented in a table file form
- `@FieldNameTranslation` to provide text that should appear in a header for this property, odrder in which this property should appear, and whether to ignore this property

It also provides writers (currently for .csv and .xls) which are using the above annotations to crreate (or append) files based on this information. Writers can write file on a disk, or return a `ByteArrayOutputStream` that can be used for example to return file with REST service.

### About `@TranslatableDTO` annotation

`@TranslatableDTO` has one property:
- `ignoreNonAnnotated = Boolean` - boolean value describing behavior regarding fields (or getters) not annotated with `@FieldNameTranslation` annotation. Default value is `true`, which means that if there is a field (or a getter without corresponding field) not annotated with `@FieldNameTranslation` annotation, it will not be printed by writer. If set to false, then all fields (or getters without corresponding fields) not annotated with `@FieldNameTranslation` annotation will be printed.

### About `@FieldNameTranslation` annotation

`@FieldNameTranslation` annotation recives three arguments:
- `value = String` - string representing desired header of this column (default - blank string)
- `order = Integer` - int value instructing writer in which order columns should appear (default - `Integer.MAX_VALUE`)
- `ignore = Boolean` - boolean value explicitly instructing writer not to print this column (default - `false`)

`@FieldNameTranslation` annotation can be applied to field or to getter method. If you have a get method that don't have corresponding field, but its name is sticking to convention (e.g `getSomething()` or `isSomethingTrue`), annotation can still be used on it.

If value is not set for `@FieldNameTranslation` annotation, it will inferred from getter name; 'get' or 'is' start is dropped, the rest is split to words by camel case. For example: if getter is named `getSomeField` derived name will be `Some Field`.

### About `ReportWriters`

Using writers is pretty straightfoward. Just autowire needed writer to your service, and you're good to go.  
Example:  

    @Autowired
    private CsvFileWriter csvFileWriter;

    public ByteArrayResource getReportAsFile(Long driverId, ReportWriterType writerType) throws IOException, InvocationTargetException, IllegalAccessException {
        List<SomeDTO> data = someRepo.fetchReport();
        ByteArrayOutputStream reportStream = csvFileWriter.writeToByteArrayOS(data, false);
        return new ByteArrayResource(reportStream.toByteArray());
    }

## How to try out this project

1. clone or download this repo
2. execute `mvn install` in report-demo-starter folder
3. setup database according to settings in report-demo-parent/report-demo-rest/src/main/resources/application.yml
4. run `mvn build` in report-demo-parent folder (this is needed to generate jooq models)
5. start report-demo-rest project
6. make a GET request `http://localhost:8080/api/report/rides-by-driver/download?driverId=1&writerType=CSV`, and in response you will receive a report file in form of .csv. Replace `writerType=CSV` with `writerType=EXCEL` in request, and in response you will receive .xls report file.

## Known limitations

Without any manual setup starter will work on Spring Boot project only.

DTOs to be translated have to be placed under the same package (or in subpackage) as the clas annotated with `@SpringBootApplication` (main class of the application)

Writer implementations provided by starter are good for printnig flat DTOs, meaning simple POJOs containing as fields only simple types, such as ints, booleans, Strings etc. Printing DTOs that contain fields of complex types, such as other DTOs, will depend on string representation of those types.

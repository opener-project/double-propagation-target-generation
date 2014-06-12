##Reference

This module is a side work to propose an additional approach to generate domain dependent potential opinion-targets unsupervisedly.
The approach only requires a bunch of texts of the target domain.
The result is a ranked list of opinion-targets (words that are likely to be target of opinions).

The technique is based on the double-propagation technique (Qiu et al. 2009) (Qiu et al. 2011) with some modifications.
More details can be found in the OpeNER deliverable D5.51.

This approach requires a syntactic dependency analysis of the sentences, which is a requirement difficult to fulfill for some languages.
The current implementation uses the Stanford NLP tools (http://nlp.stanford.edu/software/) and thus it works only for English.
It should be possible to provide an alternative implementation of the class that performs the analysis of the texts (which is isolated behind an interface) to allow working with other languages.
Even better would be to modify the codebase to admit KAF documents as input (with a dependency layer on it). KAF has a dependency layer capable of holding the depedency information, but in OpeNER no official dependency parsers have been developed for all the languages.

###Command Line Interface

First you need to have Apache Maven in your computer, so you can package the source code.

Then you can go to the folder containing the source code and the pom.xml and issue the following command:
```
mvn clean package
```

This will package the code and generate a runnable jar file.

Then you can run the process with:

```
java -jar NAME_OF_THE_JAR -i PATH_TO_INPUT_DIR -o PATH_TO_OUTPUT_DIR [-m PATH_TO_MULTIWORD_FILE]
```

* The -i parameter specifies the directory that contains the raw texts to be processed
* The -o parameter specifies the directory in which the file with the resulting opinion-targets will be generated.
* The -m parameter is optional and specifies the path to a file containing multiword terms that will be taken into account when executing the process.

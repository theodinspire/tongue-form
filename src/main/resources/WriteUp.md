Eric T Cormack<br />
CSC 594: Natural Language Processing<br />
Homework 2<br />
22 April 2017

# Language Modelling

## Installation
To build this project you will need to have installed *Maven* and the *Java
Development Kit*.

Expand this project to its destination folder. Open a terminal or command prompt
session in this `tongue-form` directory. Run:
```
mvn package
```

This has been tested on multiple devices, and should complete with no issue.

## Running the program

Once built, assuming you are in the `tongue-form` directory, you may run the
program with the following command:
```
java -jar target/tongue-form.jar -i training.txt -t test.txt -o output.txt
```
replacing `training.txt`, `test.txt` and `output.txt` with the appropriate file
names. Note that the `-o` option is optional, and if omitted, the outputted
file's name will be `output-train.txt`.

## Programming

### Data structures
For this project, I centered my implementation around the `Counter` data
structure, and its extension `WordCounter`. The `Counter` class uses a Java
`HashMap` to keep track of how many instances of a given datum it has been fed.
In order to make dealing with unknown words easier, I also included code that
will take the data from one counter and add it to another.

The `Counter` class is generalized and can be used for any object, but the
`WordCounter` is only applicable to `String`s. This allows me to specifically
replace low counted values with `<UNK>`, the `String` representing an unknown
token.

However, most of the heavy lifting is done in the classes `UnigramDistribution`
and `BigramDistribution`. The former is essentially a wrapper to the
`WordCounter` class that will compute the probability of a given token based
on the counter's total count and the target token's count.

On the other hand, the `BigramDistribution` has a lot more work to do. Here, I
have a `WordCounter` for the first words in the Bigram, and then another
`HashMap` linking these first words to their followers. When trimming unknowns
in this class, the unknowns are first taken out of the `WordCounter`. The tokens
that are removed are then used to both signify which second tokens are turned
into unknowns in the `Map` of `Counter`s, as well as which of these counters
are to be added together under the key of the unknown token. These tokens are
completely removed from this `Map`.

Essentially, my code stores the counts of these tokens as very tiny trees. This
eliminates the need to reserve memory space for zero counted bigrams. However,
it does not lend itself to an intuitive means of generating sentences from the
data collected. I have thus omitted this from the assignment.

### Program flow
After parsing the arguments for the file names, the program calls up Stanford
NLP's `DocumentPreprocessor` class to handle the tokenization of the data.
It iterates over the document at each sentence, and then again across each
sentence as a list of `HasWord`s. I've created my own special `HasWord`s that
indicate the start and end of a sentence.

It feeds each token (save the sentence start token) into the
`UnigramDistribution`, and then feeds each token along with its predecessor into
the `BigramDistribution` (here including the sentence start token as the
initial predecessor).

Once these sentences are fed into the N-gram structures, the structures are then
called to replace any token that occurs less than *ten* times in the training
data with the unknown token `<UNK>`.

After this is complete, the program calls up another `DocumentPreprocessor` to
iterate over the test corpus. Here, the N-gram structures are called to compute
the probability of a given sentence. The `BigramDistribution` is called twice
in this instance, once without smoothing and once with LaPlace smoothing.
Converting the probabilities through the natural logarithm function allows us to
avoid any integer underflow issues. Java handles nicely the *ln(0)* case with a
negative infinity value, which over powers any other cases easily and converts
back into zero when fed into an exponent. Using the length of the sentence just
calculated, all three of these probabilities are turned into perplexities.

These sentences and their data are fed into the output document as they are
computed.

## Discussion
In general, I felt that this assignment, though a fair amount of work, was
relatively straight-forward and not difficult. The idea for these data
structures presented itself immediately, and most of my coding time was spent
tweaking and finagling the output and checking numbers.

I am surprised that even with my exclusion of any datum that appears less than
ten times in the training data still resulted in so many test sentences with a
bigram probability of zero. More tests with higher thresholds would be a smart
idea.

I do wonder at the efficiency of my design. There is an appreciable pause in the
execution of the file on this data, which I think comes from my implementation
of the unknown handling. My algorithm in this instance is on the order of
*O(n &times; m)* where *n* is number of unique tokens and *m* is the number of
unique second items in the bigram starting with an element of *n*. I would like
to learn of a better solution to this.

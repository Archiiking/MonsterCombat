# Redirect Input feature of your IDE

## What is it used for?
Debugging code that depends on the users input is very time consuming, because a sequence of input manually need to be typed in order to reach the point of your interest. The Redirect Input feature solves this problem through automatically inserting a predefined sequence of inputs to the default input stream (System.in) of your program.

## Requirements
Your IDE must support this feature. Even if your IDE does support it, it might be hidden and must be activated first. See documentation for [IntelliJ](https://www.jetbrains.com/help/idea/run-debug-configuration-java-application.html#more_options) and [Eclipse](https://help.eclipse.org/latest/topic/org.eclipse.datatools.sqltools.doc.user/doc/html/asc1229700470998.html?resultof=%22%72%65%64%69%72%65%63%74%22%20%22%69%6e%70%75%74%22%20).

## How to use
Take a look at the documentations above. In general, a text file is used to define the inputs that are supposed to get simulated/redirected with each line of the file. This input file then can be selected by the Redirect Input feature in your IDEs Run/Debug Configuration. Running/Debugging this configuration then provides the desired results. When using command line arguments for your program, it might be a good idea to define multiple configurations as you might want to map your input file to specific arguments of your interest.

### Running a configuration
If you are not familiar with Run/Debug Configurations yet, here's a quick walk through. Your IDE requires some information about your code in order to run it, those are stored and configurable in mentioned Run/Debug Configurations. When your IDE detects an executable entry point (e. g. your main() method), it allows you to run your code starting at that point by automatically providing a default configuration. But as you might want to change conditions of the execution process (providing command line arguments, or using Redirect Input) this configuration can be adjusted. *Edit Configuration* usually can be found next to where you run it, or in tabs like *Run*.

## Sidenotes
* This feature does nothing more than simulating your input that you'd have to type in manually otherwise.
* Unlike when you do it manually, the input that has been simulated might not be shown in the console, causing only your programs output being shown.
* Also take a look at how to debug code in your IDE (explained in our Wiki) with break points to debug even more efficiently.
* When being stuck at a specific test case that has lots of inputs, use the protocol provided in the feedback of this case to create the input file. This can be done manually by removing each output and line prefixes (arrows indicating input or output), or with the *Replace* feature in your IDE (often CTRL + R) when matching the basic regex `((?<=-->[^\n]+)(\n(<[^\n]+))+|(<[^\n]+\n)|--> +|\\n)` and replace found matches with nothing (leave the replace field empty).
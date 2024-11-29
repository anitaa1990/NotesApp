# NotesApp
This is a demo android app on how to implement Room persistance library, making use of Kotlin Flows and Jetpack Compose.</br></br> 
<img src="https://github.com/anitaa1990/NotesApp/blob/main/media/1.png" width="200" style="max-width:100%;">  <img src="https://github.com/anitaa1990/NotesApp/blob/main/media/2.png" width="200" style="max-width:100%;">   <img src="https://github.com/anitaa1990/NotesApp/blob/main/media/3.png" width="200" style="max-width:100%;">   
<img src="https://github.com/anitaa1990/NotesApp/blob/main/media/4.png" width="200" style="max-width:100%;">   <img src="https://github.com/anitaa1990/NotesApp/blob/main/media/5.png" width="200" style="max-width:100%;">   <img src="https://github.com/anitaa1990/NotesApp/blob/main/media/6.png" width="200" style="max-width:100%;">   
<img src="https://github.com/anitaa1990/NotesApp/blob/main/media/7.png" width="200" style="max-width:100%;">   <img src="https://github.com/anitaa1990/NotesApp/blob/main/media/8.png" width="200" style="max-width:100%;">   <img src="https://github.com/anitaa1990/NotesApp/blob/main/media/9.png" width="200" style="max-width:100%;">


### App features
* Users can add a new note to the local db.
* Users can view their list of notes from the local db.
* Users can update a note to the local db.
* Users can delete a note from the local db.
* Users can lock a note so that it requires a password to view.
* Dynamic theme included.
* Rich text editor is added – so users can style their note (basic styles includes: Bold, Italics, Underline, Strikethrough, Highlight, Heading, Subheading).

### App Architecture
Based on MVI (model-view-intent) architecture.

<img src="https://github.com/anitaa1990/NotesApp/blob/main/media/10.png" width="300" style="max-width:300%;">

#### The app includes the following main components:

* A local database that servers as a single source of truth for data presented to the user. 
* A repository that works with the database, providing a unified data interface.
* An intent class that handles incoming user actions/interactions with the app.
* A ViewModel that provides data specific for the UI.
* The UI, using Jetpack Compose, which shows a visual representation of the data in the ViewModel.
* Unit Test cases for API service, Database, Repository and ViewModel.

### App Packages
* <b>model</b> 
    * <b>di</b> - contains dependency injection classes, using Hilt.
    * <b>db</b> - contains the db classes to cache network data.
    * <b>repository</b> - contains the repository classes, which acts as a bridge between the data and other classes.
 * <b>view</b> 
    * <b>ui</b> - contains compose components and classes needed to display note list and note detail screen.
* <b>intent</b> - contains classes that handles incoming user actions/interactions with the app.
* <b>composetexteditor</b> - contains classes required for a text editor to style note description.
* <b>util</b> - contains extension classes needed for date/time conversions.

#### App Specs
* Minimum SDK 26
* Written in [Kotlin](https://kotlinlang.org/)
* MVI Architecture
* Android Architecture Components (ViewModel, Room Persistence Library, Navigation Component for Compose)
* [Kotlin Coroutines]([url](https://kotlinlang.org/docs/coroutines-overview.html)) and [Kotlin Flows]([url](https://developer.android.com/kotlin/flow)).
* [Hilt]([url](https://developer.android.com/training/dependency-injection/hilt-android)) for dependency injection.
* [Gson](https://github.com/google/gson) for serialisation.
* [Mockito](https://site.mockito.org/) for implementing unit test cases

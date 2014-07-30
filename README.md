#LSStorage
###NOT an Android ORM

LSStorage removes the pain from the complex SQLite setup in Android, most of which is the same boilerplate code you have to put in every project. On top of this, LSStorage provides a simple way to map objects into SQLite, and get them back out again as objects.

This isn't a heavy ORM that completely abstracts away the underlying SQLite database. It's more of a __"do-it-yourself ORM"__. You define the database structure and how things go in and out of it. Everything is translated into nice, human-friendly SQL and the database that is created reflects that. No more databases with weird column naming that are impossible to dump and debug. LSStroage is so thin that with relative ease you can extend it with raw SQL when needs be.

To see how it's done, check out the (incomplete) tests.

- You have a collection of LSObjects (real world models), each are stored in their own LSTable
- All your LSTables live in an LSDatabase.
- You then create an LSController with all your CRUD methods in; a simple layer of abstraction between your app and the database.

TODO: 

- Would be great to have a way to do joins easily. Currently requires separate db calls (see Tests)
- See issues

__This library is not complete. It has the minimum features required to complete a couple of projects.__

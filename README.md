## Yields

Yields is an Android chat app with push notifications as well as RSS, pictures and publishing providers integrations. The chat server is working with redis and Scala (Akka, reactive streams). It can be run in production with Rancher and an ELK-stack.

This project was part of the [EPFL](http://www.epfl.ch/)'s *Software Engineering* 2015 course.

### Get started

**Client** can be run by opening `client` folder in Android Studio and launching the main. 

**Server** can be compiled using `sbt assembly` in `server` and then be executed with `java -jar server/target/scala-2.11/yields.jar` or aside with docker compose file (see `dock` folder). It also requires setting up a redis instance.

Passphrases and internet adresses will need to be adjusted in order to make everything work together.

### License

Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International (CC BY-NC-ND 4.0). See [LICENSE](./LICENSE) for more information.

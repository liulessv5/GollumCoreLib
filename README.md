GollumCoreLib
=============


It's library for Minecraft Mod. Add functionalities :

 - Blocks Factory
 - Items Factory
 - SoundRegistery
 - Building builder
 - Logger
 - Version Checker
 - Block entity auto-spawner
 - Mod metadatas getter
 - Annotation config loader
 - Several Helper 
 
 
For Install :

<pre>
./gradlew setupDecompWorkspace
./gradlew build
./gradlew eclipse
</pre>

In your eclipse launch preference add in VM Option -Dfml.coreMods.load=com.gollum.core.asm.GollumCoreLibPlugin
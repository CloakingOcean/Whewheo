# Whewheo
Advanced Teleportation System

## Introduction
### What type of program is this?
This is a spigot plugin created using Java. It's used on Minecraft servers to give developers the ability to manipulate the original Minecraft server.

### What does it do?
Whewheo is a teleportation plugin with custom Particle Generator systems. It allows server admins to create warp locations *across servers* with specific particle animations when a player is teleporting there and also once they have arrived at that location. Players are given a Warp Selector upon joining the server. This allows them to browse the various server warps available. Additionally, once they teleported, optional commands can be run on the server for extended functionality beyond the scope of this plugin.

## How does it work?
### Configuration
Like many plugins, Whewheo offers configuration for whoever owns the server. There are two files categorized by the data they contain. 

Config.yml contains general information about the styling of the Warp Selector, how big the warp menu will be, and informative messages given to users to either confirm their command, inform the users there is a previous step to be done, or when teleportation has been canceled. 

<details>
  <summary>Config.yml</summary>
  
```yaml
warpSelector:
  itemOnJoin: true
  name: '&b&lWarp Selector'
  material: '345:0'
  slot: 0
  enchantment: 'null'
  quantity: 1
  lore:
  - 'Click to Select a Warp!'
  rightClick: true
  leftClick: true
warpMenu:
  name: '&8&lWarp Selector'
  size: 45
general:
  teleportDelay: 3
  prefix: '&b[&9Whewheo&b]&7'
  bungeecordFolder: 'C:\Users\Shawn Whitaker\Documents\SpigotPlugins\1.11 Testing Server\BungeeCord'
messages:
  reloadedConfig: "Successfully reloaded config"
  createdWarp: "You have successfully added a new warp. Please update the slot and other related information in the menu.yml"
  warpAlreadyExists: "That warp already exists"
  warpAlreadyEnabled: "This warp is already enabled!"
  enabledWarp: "Enabled Warp"
  noWarpsSaved: "No warps have been saved yet"
  teleportationCancelled: "Teleportation Cancelled"
  teleportationWillCommenceIn: "Teleportation will commence in %time%"
```
</details>

Menu.yml contains information that each warp consists of. Here owners can modify various properties such as the item used in the warp menu, any lore on the item (this allows players to see a tooltip when hovering over the item), and which send and receive generators are used. Currently, Whewheo also replaces any instances of `%count%` in the lore section with the number of players of the server that warp is in. In the future, many other placeholders like this will be added.

<details>
  <summary>Menu.yml</summary>
  
```yaml
warps:
  '1':
    name: sample
    location: lobby:world:0:70:0
    enabled: true
    slot: 0
    material: '341:0'
    enchantment: 'null'
    quantity: 1
    lore:
    - ''
    - '&eThis is an'
    - '&eexample lore.'
    - '&aPlayers Online: &e%count%'
    enableCommands: true
    commands:
    - 'p:help'
    - 's:say %player% has teleported'
    sendEffect: SPIRAL
    receiveEffect: EMERALD
```
</details>

There can be a disconnect between developers and some server owners who use the plugin. There are cases where an opinionated approach to a problem may not be the wanted behavior for other owners. Having configuration files allows flexibility in the approach owners use the plugin, allowing many different servers to use the plugin in their own way.

### Internal Mechanisms
Here I will attempt to explain some of the core concepts of this plugin and why it can be considered by many to be an advanced teleportation system.

#### Generators Introduction
Each warp has a specific send generator and a specific receive generator. There are a handful of current generators to choose from. Once you have started a server with this plugin inside, it will create a generatorhelp.txt file to list all valid generation options. Currently, the send generators are `SPIRAL` and `NETHER_PORTAL`. The receive generators are `EMERALD` and `FIRE_EXPLOSION`.

![alt text](https://i.imgur.com/jhqWgcA.png "Warp Diagram")

Here's a demonstration of a send and receive generator in action.

<details>
  <summary>Spiral Send Generator</summary>

![alt text](https://i.imgur.com/N3KvlqA.png "Spiral Send Generator")
</details>


<details>
  <summary>Emerald Receive Generator</summary>

![alt text](https://i.imgur.com/rkzzhaP.png "Emerald Receive Generator")
</details>

#### How to create a Generator?
One of the major benefits of this plugin is the modular approach given to generators. It has been designed from the beginning to easily allow any number of new generators to the current list. This allows other developers to expand on my current generators and add their own designs.

##### Step 1: Create a class that extends a Generator class (SendParticleGenerator or ReceiveParticleGenerator).
<details>
  <summary>Extenstion</summary>
  
```java
public class Sphere extends SendParticleGenerator{
  private int ticks = 1;

  public Sphere(Player player, WarpTP warp) {
    // the 1 is for the tick delay.
    // In order words, how many in-game ticks to wait before running the generator again
    // 20 ticks = 1 second
    super(player, ticks, warp);
  }
}
```
</details>

#### Step 2: Override the run method. This run method is implemented from superclasses. It originates from BukkitRunnable. Here is where we will be putting our mathematical code. Because this part can get tricky, I've listed down all the implementations of the current generators so you can get a general idea of how they work.

##### Send Generators
<details>
  <summary>Spiral</summary>
  
```java
...
private double radius = 1;
...

@Override
public void run() {
    if (Bukkit.getServer().getPlayer(player.getUniqueId()) == null ) {
        ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
        cancel();
    }
    
    if (!ServerSelectionHandler.teleportingPlayers.contains(player.getUniqueId().toString())) {
        cancel();
    }
    
    Location loc = player.getLocation();
    
    int count = 1;
    
    timeInRadians = timeInRadians + Math.PI/8;
    
    
    for (int i = 0; i < 10; i++) {
        double x = (radius - (.09) * i)*Math.cos(timeInRadians + (Math.PI/8)/2 * i);
        double y = i * (Math.PI/8)/1.50;
        double z = (radius - (.09) * i)*Math.sin(timeInRadians + (Math.PI/8)/2 * i);
        loc.add(x, y, z);
        loc.subtract(x, y, z);
    }
    
    double distance = 0.2;
    
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX(), loc.getY()+ 2.7, loc.getZ()), 100);
    
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() + distance, loc.getY()+ 2.7, loc.getZ()), 100);
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() - distance, loc.getY()+ 2.7, loc.getZ()), 100);
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX(), loc.getY()+ 2.7, loc.getZ() + distance), 100);
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX(), loc.getY()+ 2.7, loc.getZ() - distance), 100);
    
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() + distance, loc.getY()+ 2.7, loc.getZ() + distance), 100);
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() + distance, loc.getY()+ 2.7, loc.getZ() - distance), 100);
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() - distance, loc.getY()+ 2.7, loc.getZ() + distance), 100);
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() - distance, loc.getY()+ 2.7, loc.getZ() - distance), 100);
    
    
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX(), loc.getY()+ 3, loc.getZ()), 100);
    
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() + 0.1, loc.getY()+ 3, loc.getZ()), 100);
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX() - 0.1, loc.getY()+ 3, loc.getZ()), 100);
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX(), loc.getY()+ 3, loc.getZ() + 0.1), 100);
    ParticleEffect.CLOUD.display(new Vector(0,0,0), 1, new Location(loc.getWorld(), loc.getX(), loc.getY()+ 3, loc.getZ() - 0.1), 100);
    
    checkTeleporation();
    
    secondsPassed += (1.0/20.0);
}
```
</details>

<details>
  <summary>Nether Portal</summary>

```java
***
private int height = 2;
private int width = 2;
final private double space = .4;
***

@Override
public void run() {
    
    if (Bukkit.getServer().getPlayer(player.getUniqueId()) == null ) {
        ServerSelectionHandler.teleportingPlayers.remove(player.getUniqueId().toString());
        cancel();
    }
    if (!ServerSelectionHandler.teleportingPlayers.contains(player.getUniqueId().toString())) {
        cancel();
    }
    Location loc = player.getLocation();
    for (double r = (width/2) * -1; r < (width/2); r += space) {
        for (double c = (height/2) * -1; c < (height/2); c += space) {
            double x = r;
            double y = 0;
            double z = c;
            Vector v = new Vector(x, 0, z);
            v = rotateAroundAxisY(v, 50 * secondsPassed);
            loc.add(v.getX(), v.getY(), v.getZ());
            ParticleEffect.SMOKE_NORMAL
            .display(0, 0, 0, 0, 1, loc, 100D);
            loc.subtract(v.getX(), v.getY(), v.getZ());
        }
    }
    
    loc.add(0, height, 0);
    for (double r = (width/2) * -1; r < (width/2); r += space) {
        for (double c = (height/2) * -1; c < (height/2); c += space) {
            double x = r;
            double y = 0;
            double z = c;
            Vector v = new Vector(x, 0, z);
            v = rotateAroundAxisY(v, 50 * secondsPassed);
            loc.add(v.getX(), v.getY(), v.getZ());
            ParticleEffect.SMOKE_NORMAL
            .display(0, 0, 0, 0, 1, loc, 100D);
            loc.subtract(v.getX(), v.getY(), v.getZ());
        }
    }
    loc.subtract(0, height, 0);
    
    for (double y = 0; y < height; y += space) {
        for (double r = (width/2) * -1; r < (width/2); r += space) {
            for (double c = (height/2) * -1; c < (height/2); c += space) {
                double x = r;
                double z = c;
                Vector v = new Vector(x, y, z);
                v = rotateAroundAxisY(v, 50 * secondsPassed);
                loc.add(v.getX(), v.getY(), v.getZ());
                
                if (x >= (width/2)- space || x <= ((width/2) * -1) + space  || 
                    z >= (height/2)- space || z <= ((height/2) * -1) + space) {
                    ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, 0, 1, loc, 100D);
                }else{
                    ParticleEffect.SPELL_WITCH.display(0, 0, 0, 0, 1, loc, 100D);
                }
                loc.subtract(v.getX(), v.getY(), v.getZ());
            }
        }
    }
    
    checkTeleporation();
    
    secondsPassed += ((double)1)/((double)20);
}
```
</details>

##### Receive Generators
<details>
  <summary>Emerald</summary>
  
 ```java
 ***
private double startyLevel = 3;
private double yLevel = 3;
private double radius = 1;
***

@Override
public void run() {
    double delay = .5;
    if (Bukkit.getServer().getPlayer(player.getUniqueId()) == null ) {
        cancel();
    }
        
    Location loc = player.getLocation();
    
    int count = 1;
    
    double tempT = timeInRadians;
    
    if (secondsPassed < delay/2) {
        radius += .1;
    }else{
        radius -= .1;
    }
    
    for (int i = 0; i < 16; i++) { //Create 16 particles
        timeInRadians = timeInRadians + Math.PI/8;
        
        //Create a circle
        double x = (radius)*Math.cos(timeInRadians);
        double y = yLevel;
        double z = (radius)*Math.sin(timeInRadians);
        loc.add(x, y, z);
        
        
        
        ParticleEffect.VILLAGER_HAPPY.display(new Vector(0, 0, 0), 1, loc, 100);
        
        loc.subtract(x, y, z);
    }
    
    yLevel -= startyLevel/((20)/2);
    
    if (secondsPassed >= delay) {
        cancel();
    }
    
    secondsPassed += (1.0/20.0);
}
 ```
</details>
<details>
  <summary>Fire Explosion</summary>

```java
***
double delay = 1;
double radius = 1.5;
***

@Override
public void run() {
    
    Location loc = player.getLocation();
    
    for (int i = 0; i < 16; i ++) {
        timeInRadians = timeInRadians + Math.PI/8;
        double x = (radius)*Math.cos(timeInRadians);
        double y = 0;
        double z = (radius)*Math.sin(timeInRadians);
        Vector v = new Vector(x, 0, z);
        v = rotateAroundAxisZ(v, 40);
        
        loc.add(v);
        loc.add(0, 1, 0);
        ParticleEffect.FLAME.display(new Vector(0, 0, 0), 1, loc, 100);
        loc.subtract(0, 1, 0);
        loc.subtract(v);
    }
    
    timeInRadians = 0;
    
    for (int i = 0; i < 16; i ++) {
        timeInRadians = timeInRadians + Math.PI/8;
        double x = (radius)*Math.cos(timeInRadians);
        double y = 0;
        double z = (radius)*Math.sin(timeInRadians);
        Vector v = new Vector(x, 0, z);
        v = rotateAroundAxisZ(v, -40);
        
        loc.add(v);
        loc.add(0, 1, 0);
        ParticleEffect.FLAME.display(new Vector(0, 0, 0), 1, loc, 100);
        loc.subtract(0, 1, 0);
        loc.subtract(v);
    }
    
    
    timeInRadians = 0;
    
    for (int i = 0; i < 16; i ++) {
        timeInRadians = timeInRadians + Math.PI/8;
        double x = (radius)*Math.cos(timeInRadians);
        double y = 0;
        double z = (radius)*Math.sin(timeInRadians);
        Vector v = new Vector(x, 0, z);
        v = rotateAroundAxisZ(v, 90);
        
        loc.add(v);
        loc.add(0, 1, 0);
        ParticleEffect.FLAME.display(new Vector(0, 0, 0), 1, loc, 100);
        loc.subtract(0, 1, 0);
        loc.subtract(v);
    }
    
    radius += .2;
    
    if (secondsPassed > delay ) {
        cancel();
    }
    
    secondsPassed += 1.0/20.0;
}
```
</details>

#### Step 3: Update ValidReceiveGenerators or ValidSendGenerators with your generator.
This allows the plugin to make a correct generators list for owners to see.

<details>
  <summary>Update Generator</summary>
  
```java
***
public enum ValidSendGenerators {
	SPIRAL(
		"Displays a white thick spiral starting from 1 block above the player's head, coming from a small circle of clouds."
	),
	
	NETHER_PORTAL(
		"Surrounds the Player in a box resembling a nether portal."
	),
	
	SPHERE(
		"Creates a sphere around the player."
	),
	***
}
```
</details>

#### Step 4: Update Main's getSendGeneratorFromEnum and getReceiveGeneratorFromEnum methods.
This allows Whewheo to understand that your new `Sphere` enum value should be mapped to your new `Sphere` object.

<details>
  <summary>Update Generator</summary>

```java
***
public static SendParticleGenerator getSendGeneratorFromEnum(ValidSendGenerators generator, Player player, WarpTP warp) {
	switch(generator) {
		case SPIRAL:
			return new Spiral(player, warp);
		case NETHER_PORTAL:
			return new NetherPortal(player, warp);
		//
		case SPHERE:
			return new Sphere(player, warp);
		//
		default:
			Bukkit.getServer().getLogger().severe("Couldn't determine matching ValidSendGenerators. Contact Developer!");
			return new Spiral(player, warp);
	}
}
***
```

</details>

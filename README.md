Wow coooooo 😮😎

[![](https://jitpack.io/v/Cyrzuu/SuperMenu.svg)](https://jitpack.io/#Cyrzuu/SuperMenu)

**Maven:** 
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.Cyrzuu</groupId>
    <artifactId>SuperMenu</artifactId>
    <version>1.1.4</version>
</dependency>
```

**Gradle:**
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
        implementation 'com.github.Cyrzuu:SuperMenu:1.1.4'
}
```





Register
```java
public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        MenuManager.registerManager(this);

        //code
    } 

} 
```

Example 
```java
public void setGameMode(Player target) {
    FastMenu fastMenu = new FastMenu(1, "Set gamemode");

    fastMenu.setButton(3,
            new ItemButton(displayName(new ItemStack(Material.IRON_SWORD), "Survival"),
                    (player, state) -> {
                        if(player.hasPermission("gamemode.survival")) player.setGameMode(GameMode.SURVIVAL);
                        else player.sendMessage("No permission!");
                    }
            ));

    fastMenu.setButton(5,
            new ItemButton(displayName(new ItemStack(Material.GRASS), "Creative"),
                    (player, state) -> {
                        if(player.hasPermission("gamemode.creative")) player.setGameMode(GameMode.CREATIVE);
                        else player.sendMessage("No permission!");
                    }
            ));

    fastMenu.start().open(target);
}
```

```java
    public void eventGame() {
        FastMenu fastMenu = new FastMenu(6, "First come first served");
        int randomSlot = fastMenu.randomSlot();

        fastMenu.setMoveableSlot(randomSlot,
            (player, stack) -> false, /*Disable put*/
            ((player, stack) -> { /*Can take only diamond*/
                if(stack.getType() == Material.DIAMOND) {
                    Bukkit.broadcastMessage(String.format("%s was the first!", player.getName()));
                    return true;
                }

                return false;
            }));

        fastMenu.setItem(randomSlot, new ItemStack(Material.DIAMOND));

        fastMenu.onClose(((player, inventory) -> {
            if(inventory.getViewers().size() <= 1) {
                fastMenu.unregister();
            }
        }));
        
        fastMenu.setUnregisterOnClose(false).start().open(new ArrayList<>(Bukkit.getOnlinePlayers()));
    }

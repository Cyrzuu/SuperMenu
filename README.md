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
    <version>2.1.2</version>
</dependency>
```

**Gradle:**
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
        implementation 'com.github.Cyrzuu:SuperMenu:2.1.2'
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
public class Class {
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

        fastMenu.open(target);
    }
}
```

```java
import org.bukkit.Bukkit;

public class Class {
    public void eventGame() {
        FastMenu fastMenu = new FastMenu(6, "First come first served");
        int randomSlot = fastMenu.randomSlot();

        fastMenu.setMoveableSlot(randomSlot,
                (player, stack) -> false, /*Disable put*/
                ((player, stack) -> { /*Can take only diamond*/
                    if (stack.getType() == Material.DIAMOND) {
                        Bukkit.broadcastMessage(String.format("%s was the first!", player.getName()));
                        return true;
                    }

                    return false;
                }));

        fastMenu.setItem(randomSlot, new ItemStack(Material.DIAMOND));

        fastMenu.onClose(((player, inventory) -> {
            if (inventory.getViewers().size() <= 1) {
                fastMenu.unregister();
            }
        }));

        fastMenu.setUnregisterOnClose(false);
        Bukkit.getOnlinePlayers().forEach(fastMenu::open);
    }
}
```

```java
public class Class {
    public void eventGame() {
        PageMenu<Material> pageMenu = new PageMenu<>(3, List.of(Material.STONE, Material.COBBLESTONE, Material.DIRT, Material.GRASS_BLOCK, Material.NETHERRACK, Material.NETHER_BRICKS,
                Material.OAK_LOG, Material.OAK_LEAVES, Material.APPLE, Material.GOLDEN_APPLE),
                (s, i) -> new StackBuilder(s).setName(s.name().toLowerCase().replace("_", " ")).build());
        pageMenu.setSlots(12, 14);

        pageMenu.setButton(0, new ItemButton(new StackBuilder(Material.ARROW).setName("Previous page").build(), (p, ib) -> {
            if(pageMenu.hasPreviousPage()) {
                send(player, Color.Sound.CLICK, 0.75);
                pageMenu.previousPage();
            }
        }));

        pageMenu.setButton(1, new ItemButton(new StackBuilder(Material.ARROW).setName("Next page").build(), (p, ib) -> {
            if(pageMenu.hasNextPage()) {
                send(player, Color.Sound.CLICK, 1.25);
                pageMenu.nextPage();
            }
        }));

        pageMenu.onClose((p, m) -> p.getGameMode() == GameMode.CREATIVE);

        pageMenu.setOnClickObject((m, p) -> p.getInventory().addItem(new ItemStack(m)));

        pageMenu.open(player);
    }
}
```
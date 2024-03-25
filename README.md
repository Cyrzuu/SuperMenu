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
<version>2.3.5</version>
</dependency>
```

**Gradle:**
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
        implementation 'com.github.Cyrzuu:SuperMenu:2.3.5'
}
```





Register

```java
import me.cyrzu.git.supermenu.SuperMenu;

public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SuperMenu.registerManager(this);

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
                new ItemButton(new ItemStack(Material.IRON_SWORD),
                        () -> {
                            if(target.hasPermission("gamemode.survival")) target.setGameMode(GameMode.SURVIVAL);
                            else target.sendMessage("No permission!");
                        }
                ));

        fastMenu.setButton(5,
                new ItemButton(new ItemStack(Material.GRASS_BLOCK),
                        state -> {
                            if(player.hasPermission("gamemode.creative")) player.setGameMode(GameMode.CREATIVE);
                            else player.sendMessage("No permission!");
                        }
                ));

        fastMenu.open(target);
    }
}
```

```java
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

        fastMenu.onClose(((player, menu) -> {
            if (menu.getInventory().getViewers().size() <= 1) {
                fastMenu.unregister();
            }

            return true;
        }));

        fastMenu.unregisterOnClose(false);
        Bukkit.getOnlinePlayers().forEach(fastMenu::open);
    }
}
```

```java
public class Class {
    public void pageMenu(Player target) {
        PageMenu<Material> pageMenu = new PageMenu<>(3, List.of(Material.STONE, Material.COBBLESTONE, Material.DIRT, Material.GRASS_BLOCK, Material.NETHERRACK, Material.NETHER_BRICKS,
                Material.OAK_LOG, Material.OAK_LEAVES, Material.APPLE, Material.GOLDEN_APPLE),
                (material, i) -> new ItemStack(material));

        pageMenu.setSlots(12, 14);

        pageMenu.setPreviousPageButton(3, new ItemStack(Material.ARROW), page -> target.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.75f, 1f));
        
        pageMenu.setNextPageButton(5, new ItemStack(Material.ARROW), page -> target.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.25f, 1f));

        pageMenu.onClose((player, menu) -> player.getGameMode() == GameMode.CREATIVE);

        pageMenu.onClickObject((player, object) -> player.getInventory().addItem(new ItemStack(object));

        pageMenu.fillBorder(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

        pageMenu.open(target);
    }
}
```

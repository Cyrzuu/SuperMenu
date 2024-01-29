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
                new ItemButton(new ItemStack(Material.IRON_SWORD),
                        (player, state) -> {
                            if(player.hasPermission("gamemode.survival")) player.setGameMode(GameMode.SURVIVAL);
                            else player.sendMessage("No permission!");
                        }
                ));

        fastMenu.setButton(5,
                new ItemButton(new ItemStack(Material.GRASS_BLOCK),
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
                (s, i) -> new StackBuilder(s).setName(s.name().toLowerCase().replace("_", " ")).build());

        pageMenu.setSlots(12, 14);

        pageMenu.setButton(0, new ItemButton(new StackBuilder(Material.ARROW).setName("Previous page").build(), (player, ib) -> {
            if (pageMenu.hasPreviousPage()) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.75f, 1f);
                pageMenu.previousPage();
            }
        }));

        pageMenu.setButton(1, new ItemButton(new StackBuilder(Material.ARROW).setName("Next page").build(), (player, ib) -> {
            if (pageMenu.hasNextPage()) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.25f, 1f);
                pageMenu.nextPage();
            }
        }));

        pageMenu.onClose((player, m) -> player.getGameMode() == GameMode.CREATIVE);

        pageMenu.setOnClickObject((m, player) -> player.getInventory().addItem(new ItemStack(m)));

        pageMenu.fillBorder(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

        pageMenu.open(target);
    }
}
```

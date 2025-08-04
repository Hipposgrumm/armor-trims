<div style="align:center"><img src="https://raw.githubusercontent.com/Hipposgrumm/armor-trims/master/images/banner.png" alt="Armor Trims" title="Do you like my white and gold armor trim?"></div><br>
Armor Trims Backport allows you to decorate your armor with various materials.<br>

# How to Add to Dev Environment
Armor Trims Backport can be added to your development environment via [CurseMaven](cursemaven.com).<br>
First add CurseMaven to your `repositories` block. Like this
```gradle
repositories {
	maven {
		url "https://cursemaven.com"
		content {
			includeGroup "curse.maven"
		}
	}
}
```

Then, add the mod to your `dependencies` block.
```
dependencies {
	implementation fg.deobf("curse.maven:armor_trims-858570:4697214") // Version 1.3.3, which is the latest at time of writing. I will try to keep this up to date.
}
```
If you don't want to use version `1.3.3`, you can always navigate to your desired version and replace the last few numbers in the dependency with the last few numbers of the file's url.
<img src="https://raw.githubusercontent.com/Hipposgrumm/armor-trims/master/images/import_tutorial_custom_install.jpg" alt="In case you can't see it, it's the numbers at the end of the url when viewing a file.">
Once you've done that, you should be able to reload your gradle project and have Armor Trims Backport readily available.<br>
<i>Note: You will probably want to make sure that the mod is present before calling any of its methods , unless this mod is a required dependency.</i>

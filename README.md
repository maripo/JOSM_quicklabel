# JOSM QuickLabel Plugin

This plugin shows specified tags next to objects.

[日本語](https://github.com/maripo/JOSM_quicklabel/blob/master/README.ja.md)

After installation, you can call the label customization dialog by choosing "View" > "QuickLabel" or shortcut Command+Shift+L. You can assign it to another key by JOSM preferences. 

Note: The menu had been located in "Data" menu in very early versions.

 ![Preferences](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/screenshot_en0.png)
 
 ## Basic usage
 
 You can set "main" and "sub" tags.
 You can specify multiple tags by writing into multiple lines in the order of descending priorities. 
 
Your customization is applied by clicking "Apply" button.
Sometimes it takes a few seconds to custom labels are displayed. 
You can switch to JOSM's usual mode by clicking "Reset" button.
 
 ## Formatted labels
 
 Tags placed between curly braces ("{" and "}") are replaced with values of corresponding tags.
 
 ### Examples
 * If a line "{level}F" is applied to an object with "level=3" tag, its label will be "3F".
 * If a line "s={smoking} w={wheelchair}" is applied to a restaurants with "wheelchair=yes, smoking=no" tags, its label will be "s=no w=yes".
 * If a line "{fire_hydrant:type}/{ref}" is applied to a fire hydrant with "fire_hydrang:type=underground, ref=221-14" tags, its label will be "underground/221-14".
 * A line "{fire_hydrant:type}/{ref}" doesn't match an object has a "fire_hydrant:type" and no "ref". 
 
 ![Preferences](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/format_example_en.png) 
 
## Options

There are some options.

* Show key-value pairs
* Always show sub tag with parentheses
* Apply on startup

## This plugin is useful when...

 * You want to complete "cuisine" tags of all restaurants in your town
 * You are interested in "surface"  or values more than "capacity" of parking areas
 * You review roads' details by comparing surface, sidewalks and maxspeed values
 * You want to edit multi-language tags by temporarily boosting priority of name:* 

## Developer

 * Maripo GODA <goda.mariko@gmail.com>
 * OSM ID: maripogoda
 * License: GPL v2 (as JOSM)
 
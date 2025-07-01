# JOSM QuickLabel Plugin

**QuickLabel** displays specified tags as labels next to map objects.

It’s ideal for mappers who want quick visual feedback for quality checks, tagging consistency, or data completeness.

[日本語](https://github.com/maripo/JOSM_quicklabel/blob/master/README.ja.md)

After installation, you can open the label customization dialog via **View > QuickLabel** or the default shortcut **Command+Shift+L**. You can assign a different shortcut in the JOSM preferences.

**Note**: In early versions, the menu was located under **Data**.

 ![Screenshot](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/top_screenshot_en.png)
 
 ## Basic usage
 
 You can set "main" and "sub" tags.
 You can specify multiple tags by entering them on separate lines, in order of descending priority.
 
Your customization is applied by clicking "Apply" button.
It may take a few seconds for custom labels to appear.
You can switch to JOSM's usual mode by clicking "Reset" button.
 
 ## Formatted labels
 
 Tags placed between curly braces ("{" and "}") are replaced with values of corresponding tags.
 
 ### Examples
 * If a line `{level}F` is applied to an object with `level=3` tag, its label will be `3F`.
 * If a line `s={smoking} w={wheelchair}` is applied to a restaurants with `wheelchair=yes, smoking=no` tags, its label will be `s=no w=yes`.
 * If a line `{fire_hydrant:type}/{ref}` is applied to a fire hydrant with `fire_hydrang:type=underground, ref=221-14` tags, its label will be `underground/221-14`.
 * A line like "{fire_hydrant:type}/{ref}" will not match an object that has a "fire_hydrant:type" tag but no "ref" tag.
 
 ![Example of custom format](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/format_example_en.png) 
 
## Options

There are some options.

 ![Options](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/options_en.png)

### Show key-value pairs

|Off|On|
|---|---|
|![Off](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/keyvalue_off_en.png)|![On](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/keyvalue_on_en.png)| 

### Always show sub-tags in parentheses
### Apply on startup

## This plugin is useful when...

 * You want to complete `cuisine` tags of all restaurants in your town
 * You care more about the `surface` than the `capacity` of parking areas
 * You review roads' details by comparing surface, sidewalks and maxspeed values
 * You want to focus on multilingual tags by temporarily prioritizing `name:*` tags

## Developer

 * Maripo GODA <goda.mariko@gmail.com>
 * OSM ID: maripogoda
 * License: GPL v2 (as JOSM)
 
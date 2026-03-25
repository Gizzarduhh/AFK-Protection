A Paper MC Plugin that prevents players from being targeted or damaged while AFK.
Has configurable timer, messages, prefixes, and suffixes!

LuckPerms is needed for suffix/prefix.

Default Config:
```yaml
# AFK Protection configuration

# Timer - # of seconds before a player is considered AFK
# Delay - # of seconds before /afk activates, anti-cheese
afk:
  timer: 120
  delay: 5

# Use %player% for display name, PlaceholderAPI optionally supported
# %delay% will only be parsed in messages.pending
messages:
  broadcast: true
  +afk: "&e%player% has gone afk"
  -afk: "&e%player% has returned"
  pending: "&7AFK in %delay% seconds..."
  canceled: "&4AFK canceled, you moved!"
  notaplayer: "&4Only players can be AFK!"

# Below options require LuckPerms
prefix:
  enabled: false
  value: "[AFK]"
  weight: 1

suffix:
  enabled: false
  value: "💤"
  weight: 1
```

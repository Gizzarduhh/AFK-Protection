A Paper MC Plugin that prevents players from being targeted or damaged while AFK.
Has configurable timer, messages, prefixes, and suffixes!

LuckPerms is needed for suffix/prefix.


Default Config:
```yaml
# AFK Protection configuration
afk:
  timer: 120

# Use %player% for display name
messages:
  enabled: true
  +afk: "%player% has gone afk"
  -afk: "%player% has returned"

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

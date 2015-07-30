# scala-robots

Library containing utilities for the robots exclusion and inclusion protocols.

### Robots exclusion protocol

The library offers facilities for parsing robots.txt files from raw strings and
building an abstract robots.txt file representation containing all the parsed
rules.

Supported directives are:
- Allow
- Disallow
- Crawl-delay
- Sitemap

For the Allow/Disallow directives, the relative URL paths may contain the
wildcard "*" character that matches any string (even the empty one) and the
end-of-string character "$" that matches the end of the URL.

In the case in which for a given URL path, more Allow/Disallow directives apply,
the most specific one (the longer directive path). If equality still holds,
the Allow directive has priority. Behaviour between wildcard paths is undefined.

Unrecognized directives are discarded and comments are ignored.

### Robots inclusion protocol

TO DO: support for Sitemaps.
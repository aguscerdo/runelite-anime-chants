#!/bin/bash

function rsid() {
  # Usage: rsid <ID> [filename]
  local SEARCH_ID="$1"
  local FILENAME="${2:-/Users/agus/Documents/coding/runelite-anime-chants/data/osrs.txt}"

  if [ -z "$SEARCH_ID" ]; then
    echo "Error: Please provide an ID."
    echo "Usage: rsid <ID> [filename]"
    return 1
  fi

  if [ ! -f "$FILENAME" ]; then
    echo "Error: File '$FILENAME' not found."
    return 1
  fi

  # Use string-based matching to avoid macOS awk slash-parsing bugs
  tr -d '\r' < "$FILENAME" | awk -v target_id="$SEARCH_ID" '
    {
      sub(/\r$/, "")
    }
    substr($0, 1, 2) == "//" {
      id_val = substr($0, 3)
      sub("^[[:space:]]+", "", id_val)
      sub("[[:space:]]+$", "", id_val)
      if (id_val == target_id) {
        found = 1
        next
      }
    }
    found && substr($0, 1, 1) == "[" && substr($0, length($0), 1) == "]" {
      print
      exit
    }
  '
}

function rsf() {
  # Usage: rsf <query_word> [filename]
  local QUERY="$1"
  local FILENAME="${2:-/Users/agus/Documents/coding/runelite-anime-chants/data/osrs.txt}"

  if [ -z "$QUERY" ]; then
    echo "Error: Please provide a query word."
    echo "Usage: rsf <query_word> [filename]"
    return 1
  fi

  if [ ! -f "$FILENAME" ]; then
    echo "Error: File '$FILENAME' not found."
    return 1
  fi

  # Use string-based functions instead of regex slashes
  tr -d '\r' < "$FILENAME" | awk -v query="$QUERY" '
    BEGIN {
      query_lower = tolower(query)
    }
    {
      sub(/\r$/, "")
    }
    substr($0, 1, 2) == "//" {
      id_line = substr($0, 3)
      sub("^[[:space:]]+", "", id_line)
      current_id = id_line
    }
    substr($0, 1, 1) == "[" && substr($0, length($0), 1) == "]" {
      name = $0
      if (index(tolower(name), query_lower) > 0) {
        print "ID: " current_id " | Name: " name
      }
    }
  '
}
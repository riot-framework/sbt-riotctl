#!/bin/bash
export GPG_TTY=$(tty)
echo HOWTO:
echo "1. publishSigned (sonatypeDrop if it fails)"
echo "2. review results in Sonatype's staging repository"
echo "3. sonatypeReleaseAll"
sbt

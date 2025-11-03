#!/bin/bash

echo "Stopping W-2 Vision Extractor..."

pkill -f 'bootRun'
pkill -f 'vite'

echo "Application stopped!"

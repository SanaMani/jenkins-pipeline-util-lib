#!/usr/bin/groovy

def create(dir) {
    bat "mkdir ${dir}"
}

def list() {
    bat 'dir'
}

def delete() {
    deleteDir()
}


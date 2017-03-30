#!/usr/bin/env ruby

require 'pty'
require 'expect'
require 'rubygems'
require 'nokogiri'

command = `which genyshell`
puts command
if command.empty?
  puts "genyshell needs to be in your path"
  exit 1
end
$expect_verbose = true
f = File.open(ARGV[0])
doc = Nokogiri::XML(f)
trackpoints = doc.css('trkpt')

coordinates = File.read(ARGV[0]).split("\n")
PTY.spawn(command) do |genny_out, genny_in, pid|
  trackpoints.each do |trkpt|
    lat = trkpt.xpath('@lat').to_s 
    lng = trkpt.xpath('@lon').to_s
    genny_out.expect(/Genymotion Shell.*/) { |r| genny_in.printf("gps setlatitude #{lat}\n") }
    genny_out.expect(/Genymotion Shell.*/) { |r| genny_in.printf("gps setlongitude #{lng}\n") }
    sleep 1.0
  end
  genny_out.expect(/gps set.*/) { |r| genny_in.printf("exit\n") }
end


# coding: utf-8


require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))
version = package['version']

source = { :git => 'https://github.com/96VuHoangDuy/react-native-netease-im.git' }
if version == '1000.0.0'
  # This is an unpublished version, use the latest commit hash of the react-native repo, which we’re presumably in.
  source[:commit] = `git rev-parse HEAD`.strip
else
  source[:tag] = "v#{version}"
end

Pod::Spec.new do |s|
  s.name                  = "RNNeteaseIm"
  s.version               = version
  s.summary            = "A React component for netease-im."
  s.homepage          = "https://github.com/96VuHoangDuy/react-native-netease-im"
  s.requires_arc       = true
  s.license                = "None"
  s.author                 = {"DavidVu" => "duy.vu@just.engineer"}
  s.platform              = :ios , "12.0"
  s.source                 = source
  s.source_files        = "**/*.{h,m}"

  s.dependency 'React-Core'
  s.dependency "NIMSDK", "9.12.1"
  
end

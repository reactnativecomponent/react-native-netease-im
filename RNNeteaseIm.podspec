# coding: utf-8


require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))
version = package['version']

source = { :git => 'https://github.com/reactnativecomponent/react-native-netease-im.git' }
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
  s.homepage          = "https://github.com/reactnativecomponent"
  s.requires_arc       = true
  s.license                = "None"
  s.author                 = {"Y_Kinooo" => "75438777@qq.com"}
  s.platform              = :ios , "8.0"
  s.source                 = source
  s.source_files        = "**/*.{h,m}"

  s.dependency 'React-Core'
  s.dependency "NIMSDK", "6.2.0"
  
end

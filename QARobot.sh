# 机器人地址
RobotURL="https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=053f21b8-5c5d-4445-8c06-e790cf1375b8"

DownloadURL="https://www.pgyer.com/8fOn"

for line in $(cat CHANGELOG.MD)
do
  changelog=$changelog$line" \\n"
done

echo $changelog

application_info=`aapt dump badging app/build/outputs/apk/app.apk`
echo "application_info: $application_info"

package_name=`echo $application_info | sed "s/^.*package: name='//g" | sed "s/' versionCode.*$//g"`
version_code=`echo $application_info | sed "s/^.*versionCode='//g" | sed "s/' versionName.*$//g"`
version_name=`echo $application_info | sed "s/^.*versionName='//g" | sed "s/' compileSdkVersion.*$//g"`
application_label=`echo $application_info | sed "s/^.*application: label='//g" | sed "s/' icon.*$//g"`

# 项目标题
ProjectTitle="$application_label Android 版本更新"

echo "package_name: $package_name"
echo "version_code: $version_code"
echo "version_name: $version_name"

curl "$RobotURL" \
 -H "Content-Type: application/json" \
-d "{ \
    \"msgtype\":\"markdown\",\
    \"markdown\":
{
\"content\":\" **$ProjectTitle**\n\n\n \
-  Package Name: $package_name\n\n\n \
-  Version Code: $version_code\n\n\n \
-  Version Name: $version_name\n\n\n \
-  下载地址:    [QA版本]($DownloadURL)\n\n\n \
-  更新内容：\n \
> $changelog\"
}
    }"

exit 0;
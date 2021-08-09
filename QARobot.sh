# 机器人地址
# 智家QA发布

RobotURL="https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=053f21b8-5c5d-4445-8c06-e790cf1375b8"

# 项目标题
ProjectTitle="艾拉工程 Android QA版本 更新"
DownloadURL="https://www.pgyer.com/ayla_test_package"
changelog=$(cat CHANGELOG.MD | head -n 6)


echo $changelog
curl "$RobotURL" \
 -H "Content-Type: application/json" \
-d "{ \
    \"msgtype\":\"markdown\",\
    \"markdown\": \
        {   \
            \"content\":\" **$ProjectTitle** \n\n\n    Version: $1 \n    下载地址:[$DownloadURL]($DownloadURL) \n\n\n    更新内容：\n > $changelog \n \"
        }   \
    }"

exit 0;
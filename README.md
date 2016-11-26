# androidDesignEx

##包含两个Design的扩展

-  1 ParallaxScaleBehavior 下拉缩放CollapsingToolbarLayout中的image
-  2 CollapsingCenterLayout 居中显示title的CollapsingToolbarLayout

<img src="https://github.com/anzewei/Android-DesignEx/blob/master/ext/ezgif.com-resize.gif" />
# Demo Apk

<a href="https://github.com/anzewei/Android-DesignEx/blob/master/ext/sample-debug.apk?raw=true">下载</a>

# 使用
[ ![Download](https://api.bintray.com/packages/anzewei/maven/com.github.anzewei.design/images/download.svg) ](https://bintray.com/anzewei/maven/com.github.anzewei/0.3)

## Step 1

- 添加以下代码到 build.gradle

``` groovy
compile 'com.github.anzewei:androidDesignEx:0.3'
``` 
	
## ParallaxScaleBehavior的使用

-  为AppBarLayout 设置Behavior,并且在CollapsingToolbarLayout中需要包含一个id为image的view，这个view用于缩放

```xml
    <android.support.design.widget.AppBarLayout
        android:id="@+id/appbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_behavior="android.support.design.widget.ParallaxScaleBehavior">
		<android.support.design.widget.CollapsingToolbarLayout>
			<ImageView 
               android:id="@id/image"/>
		</android.support.design.widget.CollapsingToolbarLayout>
	</android.support.design.widget.AppBarLayout
```
## CollapsingCenterLayout的使用
	CollapsingCenterLayout继承自CollapsingToolbarLayout，用法和CollapsingToolbarLayout相同
	
```xml
   <android.support.design.widget.AppBarLayout
        android:id="@+id/appbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_behavior="android.support.design.widget.ParallaxScaleBehavior">
		<com.github.anzewei.design.CollapsingCenterLayout>
			...
		</com.github.anzewei.design.CollapsingCenterLayout>
	</android.support.design.widget.AppBarLayout
```


# License

Copyright 2015 anzewei

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

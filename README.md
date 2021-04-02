# Weather
使用MVP架构实现了一个天气软件，使用天气API网站提供的数据，并调用必应每日一图作为背景。
![image](README/Weather1.png)
View层负责界面的刷新和事件的传递，Presenter层处理View层的事件并向Model层请求数据，Model层回调返回数据，
Presenter层调用View层的引用来刷新数据。
效果如下：
![image](README/Weather2.jpg)
![image](README/Weather3.jpg)
Ps:UI布局参考《第一行代码》

# Weather
使用MVP架构实现了一个天气软件，使用天气API网站提供的数据，并调用必应每日一图作为背景。
<img src="https://github.com/RArchered/Weather/blob/main/README/Weather1.png" width="200"  alt="项目架构"/>

View层负责界面的刷新和事件的传递，Presenter层处理View层的事件并向Model层请求数据，Model层回调返回数据，
Presenter层调用View层的引用来刷新数据。
效果如下：
<img src="https://github.com/RArchered/Weather/blob/main/README/Weather2.jpg" width="200"  alt="效果1"/>

<img src="https://github.com/RArchered/Weather/blob/main/README/Weather3.jpg" width="200"  alt="效果2"/>
Ps:UI布局参考《第一行代码》

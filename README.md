# Requirements

* Java >= 6
* Ant  >= 1.8

# Run a demo

インターネットへの接続に Proxy 設定が必要である場合は、ファイル build.properties の以下のプロパティを編集する。

    proxy.host = &lt;Proxy Server Address&gt;
    proxy.port = &lt;Proxy Server Port&gt;

以下のように Ant を実行すると、デモアプリが動作する。エラーが発生しなければ、ブラウザで http://localhost:8080/filechooser-example/index.html を開く。

    % ant start

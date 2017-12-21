# JOSM QuickLabel Plugin

好きなタグの値をオブジェクトに表示することができます。

[English](https://github.com/maripo/JOSM_quicklabel/blob/master/README.md)

インストールすると、"表示" > "QuickLabel" またはショートカット Command+Shift+Lでラベルをカスタマイズするウィンドウを表示することができます。

注: 初期のバージョンでは "データ" メニューに含まれておりましたが、より適切な "表示" の下に移動しました。


 ![Preferences](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/screenshot_en0.png)
 
 ![Preferences](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/screenshot_en1.png)
 
 ![Preferences](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/screenshot_en2.png)
 
## 設定方法

「メイン」と「サブ」の2種類があります。各テキストボックスに優先度順にタグを記入してください。


## オプション

オプションによって表示のされ方が変わります。

 ![Preferences](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/options_ja.png)


### サブのタグを必ず括弧に入れて表示する
通常、メインに該当するタグを持たずサブに該当するタグだけがある場合、サブのタグだけが表示されます。「サブのタグを必ず括弧に入れて表示する」オプションをオンにすると、そのような場合にも括弧に入って表示されます。

### キーと値を両方表示する
このオプションにチェックを入れると、値だけでなく、「キー=値」という形式で表示されるようになります。どのキーがどの値なのかをはっきりさせたい場合にお使いください。

### 起動時に適用する
このオプションにチェックを入れると、JOSM起動時に即座にQuickLabelの設定が適用されます。チェックしないでおくと、次回起動時に再びQuickLabelダイアログを開いて「適用」を押さないと適用されません。
 
## 役に立つシチュエーション

 * 地域の飲食店の "cuisine" タグを充実させたいとき
 * 駐車場の "capacity" よりも "parking" や "surface" タグをチェックしたいとき
 * 地域全体の道路の路面・速度制限・歩道などの情報を網羅的にレビューしたいとき
 * 一時的に多言語の名称を優先的に表示し、多言語対応を進めたいとき

# 開発者

 * Maripo GODA / ごうだまりぽ <goda.mariko@gmail.com>
 * OSM ID: maripogoda
 * License: GPL v2 (as JOSM)
 
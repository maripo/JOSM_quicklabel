# JOSM QuickLabel Plugin

好きなタグの値をオブジェクトに表示することができます。

[English](https://github.com/maripo/JOSM_quicklabel/blob/master/README.md)

インストールすると、"表示" > "QuickLabel" またはショートカット Command+Shift+L (このキーバインドはJOSMの設定で他のキーに割り当てることも可能です) でラベルをカスタマイズするウィンドウを表示することができます。

注: 初期のバージョンでは "データ" メニューに含まれておりましたが、より適切な "表示" の下に移動しました。

 ![Preferences](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/screenshot_en0.png)
 
## 基本的な使い方

「メイン」と「サブ」の2種類があります。各テキストボックスに優先度順にタグを記入してください。
メインに該当するタグが存在しない場合は、サブのタグだけが表示されます。

「適用」をクリックすると、設定が反映されます。「通常表示に戻す」をクリックすると、QuickLabelの設定ではなくJOSMの通常のラベルが表示されます。

## 進んだ記法

メインおよびサブのタグの行に {} で囲んだ箇所があると、それは普通のタグではなく、テキストの一部を該当するタグの値で置き換えたものが表示されるようになります。
一行に複数の {} を含めることができ、{} に囲まれたタグすべてに該当するオブジェクトに適用されます。

### 例 
* "{building:levels}階建" という行があると、"building:levels=8" というタグのついた建物には "8階建" と表示されます。
* "{addr:neighbourhood}{addr:block_number}-{addr:housenumber}" という行があると、"addr:neighbourhood=1丁目, addr:block_number=3, addr:housenumber=5" というタグのついたオブジェクトに "1丁目3-5" と表示されます。
* "車椅子={wheelchair} 喫煙={smoking}" という行があると、"wheelchair=yes, smoking=no" というタグのついたオブジェクトには "車椅子=yes 喫煙=no" と表示されます。
 
 ![Preferences](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/screenshot_en1.png)
 
 ![Preferences](https://raw.githubusercontent.com/maripo/JOSM_quicklabel/master/doc/img/screenshot_en2.png)

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
 
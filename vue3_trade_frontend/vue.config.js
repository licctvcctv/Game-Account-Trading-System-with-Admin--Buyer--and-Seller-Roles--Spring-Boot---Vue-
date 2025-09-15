const { defineConfig } = require("@vue/cli-service");
module.exports = defineConfig({
  transpileDependencies: true,
  css: {
    loaderOptions: {
      // 没有分号会报错
      sass: {
        // data: '@import "@/assets/css/vars.scss";' //旧版sass-loader写法(8.0以下)
        additionalData: `@import "@/styles/variable.scss";` //新版scss-loader(8.0及以上)
      }
    }
  },
  devServer: {
    client: {
      overlay: false
    }
  },
  chainWebpack: config => {
    // 额外确保 CopyWebpackPlugin 不会复制 public/index.html，避免与 HtmlWebpackPlugin 输出冲突
    config.plugin('copy').tap(args => {
      if (args && args[0] && Array.isArray(args[0].patterns) && args[0].patterns[0]) {
        const patt = args[0].patterns[0];
        patt.globOptions = patt.globOptions || {};
        const ignores = new Set([...(patt.globOptions.ignore || [])]);
        ignores.add('**/index.html');
        patt.globOptions.ignore = Array.from(ignores);
      }
      return args;
    });
  }
});

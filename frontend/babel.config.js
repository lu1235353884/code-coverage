/*
是生产环境
是预览模式
 */
const IS_PROD = ['production', 'prod'].includes(process.env.NODE_ENV)
const IS_PREVIEW = process.env.VUE_APP_PREVIEW === 'true'

/*
定义了一个空数组 plugins，用于存放 Babel 插件配置
 */
const plugins = []

//是生产环境且不是预览模式
if (IS_PROD && !IS_PREVIEW) {
  // 'transform-remove-console' 插件推入 plugins 数组中。这个插件的作用是在 Babel 处理代码时移除所有的 console 语句，以便在生产环境中减少日志输出
  plugins.push('transform-remove-console')
}

/*
推入了一个配置数组到 plugins 中，这个配置是用于 babel-plugin-import 插件。该插件用于优化 ant-design-vue（一个流行的 Vue UI 组件库）的按需加载。
配置指示 Babel 在编译过程中自动引入组件库中所需的组件和相应的样式文件（在这里是 less 文件，因为 style 设置为 true）
 */
// lazy load ant-design-vue
// if your use import on Demand, Use this code
plugins.push(['import', {
  'libraryName': 'ant-design-vue',
  'libraryDirectory': 'es',
  'style': true // `style: true` 会加载 less 文件
}])

/*
导出 Babel 配置对象
presets：预设的数组。这里使用了 Vue CLI 的 Babel 预设 @vue/cli-plugin-babel/preset 和 @babel/preset-env。@babel/preset-env 预设配置了 useBuiltIns: 'entry' 和 corejs:
3，这意味着 Babel 会根据你的浏览器目标和你的代码中的实际使用情况，引入必要的 polyfills（通过 core-js 版本 3）。
plugins：上面定义并可能添加了插件的数组。
 */
module.exports = {
  presets: [
    '@vue/cli-plugin-babel/preset',
    [
      '@babel/preset-env',
      {
        'useBuiltIns': 'entry',
        'corejs': 3
      }
    ]
  ],
  plugins
}


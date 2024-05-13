const {join, resolve} = require('path')

const argv = require('yargs').argv
const HtmlWebpackPlugin = require('html-webpack-plugin')
const variables = require('@jetbrains/ring-ui/extract-css-vars')
const ringUiWebpackConfig = require('@jetbrains/ring-ui/webpack.config')

const pkgConfig = require('./package.json').config

const componentsPath = join(__dirname, pkgConfig.components)

const {mode} = argv
const destination = mode === 'production' ? 'production' : 'development'

// Patch @jetbrains/ring-ui svg-sprite-loader config
ringUiWebpackConfig.loaders.svgSpriteLoader.include.push(
  require('@jetbrains/logos'),
  require('@jetbrains/icons'),
)

const webpackConfig = () => ({
  entry: `${componentsPath}/App/App.js`,
  resolve: {
    mainFields: ['module', 'browser', 'main'],
    alias: {
      env: resolve(`./src/env/${destination}`),
      react: resolve('./node_modules/react'),
      'react-dom': resolve('./node_modules/react-dom'),
      '@jetbrains/ring-ui': resolve('./node_modules/@jetbrains/ring-ui'),
    },
  },
  output: {
    path: resolve(__dirname, pkgConfig.dist),
    filename: '[name].js',
    publicPath: '',
    devtoolModuleFilenameTemplate: '/[absolute-resource-path]',
  },
  module: {
    rules: [
      ...ringUiWebpackConfig.config.module.rules,
      {
        test: /\.css$/,
        include: componentsPath,
        use: [
          'style-loader',
          {loader: 'css-loader?modules&localIdentName=[name]__[local]___[hash:base64:5]'},
          {
            loader: 'postcss-loader',
            options: {
              config: {
                ctx: {variables},
              },
            },
          },
        ],
      },
      {
        // Loaders for any other external packages styles
        test: /\.css$/,
        include: /node_modules/,
        exclude: ringUiWebpackConfig.componentsPath,
        use: ['style-loader', 'css-loader'],
      },
      {
        test: /\.js$/,
        include: [componentsPath, join(__dirname, './src/contexts')],
        loader: 'babel-loader?cacheDirectory',
      },
    ],
  },
  devServer: {
    stats: {
      assets: false,
      children: false,
      chunks: false,
      hash: false,
      version: false,
    },
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: 'html-loader?interpolate!src/index.html',
    }),
  ],
})

module.exports = webpackConfig

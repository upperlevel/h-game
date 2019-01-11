const path = require("path");

const HtmlWebpackPlugin = require("html-webpack-plugin");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const TerserPlugin = require("terser-webpack-plugin");

module.exports = {
    mode: "development",
    devtool: "inline-source-map",

    entry: "./src/index.ts",
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: "ts-loader",
                exclude: /node_modules/
            }
        ]
    },
    resolve: {
        extensions: [".tsx", ".ts", ".js"]
    },
    output: {
        filename: "[name].[contenthash].js",
        path: path.resolve(__dirname, "dist")
    },
    plugins: [
        new HtmlWebpackPlugin({
            title: "H-Game",
            filename: "index.html",
            template: "src/index.html"
        }),
        new CopyWebpackPlugin([{
            from: "assets",
            to: "assets"
        }]),
    ],
    optimization: {
        splitChunks: {
            chunks: "all",
        },
        minimizer: [
            new TerserPlugin({
                terserOptions: {
                    compress: {
                        pure_funcs: [ "console.trace", "console.log", "console.debug" ]
                    },
                    output: {
                        comments: false
                    },
                }
            })
        ]
    }
};

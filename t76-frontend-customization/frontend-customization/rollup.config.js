import * as node_sass from 'node-sass';
import commonjs from 'rollup-plugin-commonjs';
import copy from 'rollup-plugin-copy';
import json from 'rollup-plugin-json';
import resolve from 'rollup-plugin-node-resolve';
import replace from 'rollup-plugin-replace';
import sass from 'rollup-plugin-sass';
import sourceMaps from 'rollup-plugin-sourcemaps';
import typescript from 'rollup-plugin-typescript';
import {uglify} from 'rollup-plugin-uglify';

const production = !process.env.ROLLUP_WATCH;

export default {
    input: 'src/index.tsx',
    output: {
        name: 'flowable.externals',
        file: './dist/custom.js',
        format: 'umd',
        sourcemap: true,
        globals: {
            react: 'flowable.React',
            'react-dom': 'flowable.ReactDOM',
            '@flowable/forms': 'flowable.Forms',
            '@flowable/external': 'flowable.External'
        }
    },
    plugins: [
        replace({
            'process.env.NODE_ENV': JSON.stringify( 'production' )
        }),
        resolve(),
        commonjs(),
        json(),
        sass({
            output: './dist/custom.css',
            runtime: node_sass,
            options: {
                outputStyle: production ? 'compressed' : 'expanded'
            }
        }),
        typescript(),
        production && uglify(),
        sourceMaps(),
        copy({ dist: '../../flowable-work-customization/src/main/resources/static/ext' })
    ],
    external: ['react', 'react-dom', 'react-is', 'react-markdown', '@flowable/forms', '@flowable/external']
};
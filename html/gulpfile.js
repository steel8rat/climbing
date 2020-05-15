const { src, dest } = require("gulp");
const concat = require('gulp-concat');
const minify = require('gulp-minify');

function concatJs(cb) {
    src('./js/*.js')
        .pipe(concat('bundle.js'))
        .pipe(minify())
        .pipe(dest('public/build/js'));
    cb();
}

exports.concatJs = concatJs;
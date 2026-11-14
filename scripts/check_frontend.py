#!/usr/bin/env python3
"""Lightweight checks for local links and basic page accessibility metadata."""

from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import unquote, urlsplit

ROOT = Path(__file__).resolve().parents[1] / "frontend"
ERRORS: list[str] = []


class PageParser(HTMLParser):
    def __init__(self, page: Path) -> None:
        super().__init__(convert_charrefs=True)
        self.page = page
        self.ids: set[str] = set()
        self.fragments: list[str] = []
        self.title = False
        self.main = False
        self.lang = False
        self.in_title = False

    def handle_starttag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        attributes = dict(attrs)
        if tag == "html":
            self.lang = bool(attributes.get("lang"))
        if tag == "title":
            self.in_title = True
        elif tag == "main":
            self.main = True

        element_id = attributes.get("id")
        if element_id:
            if element_id in self.ids:
                ERRORS.append(f"{self.page.relative_to(ROOT)}: id duplicado: {element_id}")
            self.ids.add(element_id)

        if tag == "img" and attributes.get("alt") is None:
            ERRORS.append(f"{self.page.relative_to(ROOT)}: imagem sem atributo alt")

        for attribute in ("href", "src"):
            value = attributes.get(attribute)
            if tag == "a" and attribute == "href" and value is not None and not value.strip():
                ERRORS.append(f"{self.page.relative_to(ROOT)}: link sem destino")
                continue
            if not value:
                continue
            if value in ("#", "..."):
                ERRORS.append(f"{self.page.relative_to(ROOT)}: destino de link placeholder: {value}")
                continue
            parts = urlsplit(value)
            if parts.scheme or parts.netloc:
                continue
            if parts.fragment:
                self.fragments.append(parts.fragment)
            local_path = unquote(parts.path)
            if not local_path:
                continue
            target = (self.page.parent / local_path).resolve()
            if not target.is_file():
                target = (ROOT.parent / local_path.removeprefix("./")).resolve()
            if not target.is_file():
                ERRORS.append(f"{self.page.relative_to(ROOT)}: arquivo local ausente: {local_path}")

    def handle_endtag(self, tag: str) -> None:
        if tag == "title" and self.in_title:
            self.title = True
            self.in_title = False


def main() -> int:
    pages = sorted(ROOT.glob("*.html"))
    if not pages:
        ERRORS.append("Nenhuma página HTML encontrada em frontend/")

    parsed: dict[Path, PageParser] = {}
    for page in pages:
        parser = PageParser(page)
        parser.feed(page.read_text(encoding="utf-8"))
        parsed[page] = parser
        relative = page.relative_to(ROOT)
        if not parser.lang:
            ERRORS.append(f"{relative}: atributo lang ausente no elemento html")
        if not parser.title:
            ERRORS.append(f"{relative}: elemento title ausente ou vazio")
        if not parser.main:
            ERRORS.append(f"{relative}: landmark main ausente")

    for page, parser in parsed.items():
        for fragment in parser.fragments:
            if fragment not in parser.ids:
                ERRORS.append(f"{page.relative_to(ROOT)}: âncora local sem destino: #{fragment}")

    if ERRORS:
        print("\n".join(f"ERRO: {error}" for error in ERRORS))
        return 1
    print(f"Verificação front-end concluída: {len(pages)} páginas, sem links locais quebrados ou IDs duplicados.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

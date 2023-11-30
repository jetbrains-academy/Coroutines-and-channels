import os
import re

import requests
from markdown import markdown
import argparse


def read_file(filename):
    try:
        with open(filename, 'r') as file:
            content = file.read()
            return content
    except FileNotFoundError:
        print(f"File '{filename}' not found.")
        return None


def extract_links_from_markdown(markdown_text):
    if markdown_text is None:
        return [], []

    html = markdown(markdown_text)

    link_pattern = '<a\s+(?:[^>]*?\s+)?href="([^"]*)"'
    image_pattern = '<img\s+(?:[^>]*?\s+)?src="([^"]*)"'

    links = re.findall(link_pattern, html)
    images = re.findall(image_pattern, html)

    return links, images


def find_files(directory, filename):
    files_list = []

    for root, directories, files in os.walk(directory):
        for file in files:
            if file == filename:
                files_list.append(os.path.join(root, file))

    return files_list


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--dir', type=str, help='Path to course directory', default='./')
    args = parser.parse_args()

    course_directory = args.dir
    task_description_name = 'task.md'
    course_link_prefix = 'course://'

    print("\n===== Common info =====\n")
    print(f"Running for directory {course_directory} a.k.a.  {os.path.abspath(course_directory)}")

    task_files = find_files(course_directory, task_description_name)

    print(f"\n===== The following links were found and processing =====\n")
    errors_log = ""

    for file in task_files:
        print(f"FILE: {file}")
        links, images = extract_links_from_markdown(read_file(file))
        task_folder = file[:file.rfind("/")]

        # checking links to local files and web resources
        for link in links:
            print(f"\t LINK: {link}")
            if link.startswith(course_link_prefix):
                internal_resource_link = link[len(course_link_prefix):]
                internal_resource_path = f"{course_directory}/{internal_resource_link}"
                if not (os.path.isfile(internal_resource_path) or os.path.isdir(internal_resource_path)):
                    print(f"Error in file: {file}")
                    print(f"\tNO such file: {internal_resource_path}")
                    errors_log+=f"Error in file: {file}\n\tNO such file: {internal_resource_path}"
            else:  # try to interpret link as an url
                response = requests.head(link)
                if response.status_code != 200:
                    print(f"Error in file: {file}")
                    print(f"\tNOT valid url (returns {response.status_code}): {link}")
                    errors_log+=f"Error in file: {file}\n\tNOT valid url (returns {response.status_code}): {link}"

        # checking images
        for image in images:
            print(f"\t IMAGE: {image}")
            image_path = f"{task_folder}/{image}"
            if not os.path.isfile(image_path):
                print(f"Error in file: {file}")
                print(f"\tNO such file: {image_path}")
                errors_log+=f"Error in file: {file}\n\tNO such file: {image_path}"

    print("\n===== ERRORS LOG =====\n")
    if len(errors_log) > 0:
        print(errors_log)
        exit(1)
    else:
        print("No errors found")
        exit(0)

# TODO:
# BUG: image links can be URLs
# Support another types of links:
    # file://
    # psi_element://
    # tool_window://
    # settings://